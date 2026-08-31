# ADR-001 · Idempotência em operações que movem valor

- **Status:** aceito
- **Data:** 2026-08-29
- **Contexto:** T-006 · `POST /v1/gift-card-orders`
- **Afeta:** todos os endpoints financeiros (emissão, ativação, autorização, captura, reembolso, prorrogação)

---

## Contexto

Um cliente envia `POST /v1/gift-card-orders`, o serviço processa e commita, e a
resposta se perde — timeout de load balancer, conexão caída, pod reiniciado
depois do commit. O cliente não sabe se funcionou e reenvia.

Sem proteção, isso cria dois pedidos. E **não há concorrência envolvida**: as
requisições podem estar a trinta segundos de distância. Nenhum lock,
`@Version` ou constraint sobre dados de negócio resolve.

O ponto que fecha as alternativas: **não é possível distinguir retentativa de
compra nova pelo conteúdo**. Dois pedidos de R$ 300 do mesmo comprador no mesmo
minuto são perfeitamente válidos — um vale para cada sobrinho. Não existe
combinação de campos de negócio que separe os dois casos. Só o cliente sabe, e
ele comunica isso reusando a mesma `Idempotency-Key`.

---

## Decisão

### 1. Chave gerada pelo cliente, obrigatória em todo POST que move valor

Header `Idempotency-Key`, 16 a 64 caracteres. Não existe endpoint que a emita:
se o serviço gerasse, o cliente receberia uma chave nova na retentativa e o
problema apenas mudaria de lugar.

O contrato exige que a chave seja gerada **uma vez, antes da primeira
tentativa, e reusada em todas as retentativas da mesma operação**. Ela pertence
à intenção, não à tentativa HTTP.

### 2. Tabela `idempotency_record` com duas funções

**Sentinela** — a PK é o que fisicamente impede a segunda execução. Não é o
`if` no código: sob concorrência duas threads atravessam o `if` juntas, e
nenhuma atravessa o índice único.

**Cache da resposta** — a retentativa recebe a mesma resposta, com o mesmo
`orderId`. Devolver "409 já existe" deixaria o cliente preso: ele não saberia o
id do pedido que ele próprio criou, e não teria como seguir para o pagamento.

### 3. Claim atômico via `INSERT ... ON CONFLICT DO NOTHING`

**A tentativa de inserir É a verificação.** Não existe consultar-e-depois-inserir:
duas requisições simultâneas veriam vazio juntas e ambas seguiriam. Mesma
diferença entre `containsKey` seguido de `put` e `putIfAbsent`.

1 linha afetada significa primeira execução; 0 significa que a chave já existe.

`DO NOTHING` e não `DO UPDATE`: o segundo traria `RETURNING` de graça, mas
sobrescreveria o registro original — que é exatamente o que precisa ser
preservado para o replay.

### 4. `JdbcClient` no claim, JPA no resto

JPA não expressa `ON CONFLICT DO NOTHING`. E no PostgreSQL uma violação de
constraint aborta a transação inteira, então o padrão "tenta `persist`, se
falhar lê o existente" **não funciona** numa transação única — o próximo
comando recebe `current transaction is aborted`.

A escolha fica contida no adapter. O caso de uso vê apenas a porta.

Some-se que a tabela não recebe nada do ORM: sem relacionamento, sem lazy
loading, sem grafo, acesso só por chave primária. É uma linha chave-valor.

### 5. Transação única, sem coluna de estado

Claim, persistência do pedido e gravação da resposta acontecem na mesma
transação, aberta pelo caso de uso.

Isso dá a propriedade central: **ou os dois registros existem, ou nenhum**.
Crash em qualquer ponto faz rollback conjunto e a retentativa reprocessa limpo.
Não há estado intermediário possível.

Em consequência, a tabela **não tem coluna de estado**: em MVCC a linha não
commitada é invisível para outras transações, então ninguém leria um
`IN_PROGRESS`. E sem estado intermediário não existe registro órfão, logo não é
preciso job de limpeza de "em processamento".

### 6. Fingerprint SHA-256 do command canonizado

Detecta cliente com bug reusando a chave para conteúdo diferente. Sem ele, quem
pedisse R$ 50 com uma chave já usada receberia `200` com o pedido de R$ 300 que
criou antes — e ninguém perceberia até a conciliação não fechar.

Seis campos em ordem fixa: versão do algoritmo, `amountCents`, `purchaserRef`,
`recipient.name`, `recipient.email`, `recipient.message`.

**Não entram** `X-Correlation-Id` nem timestamps do cliente: mudam
legitimamente entre tentativas, e incluí-los faria **toda** retentativa
devolver `422`.

O prefixo de versão fica **no código**, ao lado da função que o usa, e não em
configuração — em `application.yml` alguém mudaria `v1` para `v2` e criaria
duas gerações incompatíveis sem mudança rastreável no repositório.

O fingerprint **não é único no banco**. Único significaria proibir dois pedidos
de mesmo conteúdo, que é justamente a deduplicação por conteúdo que este
desenho rejeita.

### 7. Ramificação quando o claim falha

| Situação | Resposta |
|---|---|
| `load` volta vazio | `409 IN_PROGRESS` |
| `endpoint` diferente | `422 IDEMPOTENCY_KEY_REUSE` |
| `fingerprint` diferente | `422 IDEMPOTENCY_KEY_REUSE` |
| tudo igual | `200` com `response_status` e `response_body` gravados |

O último é o caminho principal — a razão de a tabela existir.

`endpoint` é verificado **antes** do fingerprint: se a chave veio de outra
operação os fingerprints divergem de qualquer forma, mas "pertence a outro
endpoint" é muito mais útil para quem depura.

O `409` corresponde a claim que falhou **e** linha que não aparece —
contraditório só na aparência: existe transação concorrente não commitada,
invisível por MVCC. É o duplo clique, ou dois pods com a mesma retentativa.

### 8. `409 IN_PROGRESS` mantido, mesmo possivelmente inalcançável

Não foi verificado se `INSERT ... ON CONFLICT DO NOTHING` **bloqueia** até a
transação concorrente commitar ou **devolve zero linhas** de imediato.

- Se devolve zero linhas, o `load` vazio acontece e o `409` é o caminho normal
  da concorrência.
- Se bloqueia, a segunda requisição espera, lê o registro já completo e faz
  replay. O `409` nunca é emitido.

O caminho fica no contrato e no código por precaução. Custa pouco e cobre
mudança de comportamento do banco, configuração futura de `lock_timeout`, ou a
migração para multi-transação descrita em "Consequências".

**Registrado explicitamente para que quem encontrar um caminho sem cobertura de
teste saiba que foi escolha, não esquecimento.** A métrica
`giftcard.idempotency.rejected{reason="in_progress"}` responde a pergunta com
tráfego real.

### 9. Replay byte-a-byte

`response_body` é `text` e não `jsonb`: `jsonb` descarta ordem de chaves e
normaliza espaço em branco, o que quebraria a igualdade exata — necessária para
cliente que assina o payload.

A resposta é serializada **uma vez** no caso de uso e a mesma `String` é
gravada e devolvida. Por isso o controller retorna `String` e não DTO:
reserializar na volta pode divergir do gravado por configuração de
`ObjectMapper`, `@JsonInclude` ou ordem de campos.

### 10. Escopo da chave: `(idempotency_key)`, com `endpoint` validado

A PK é só a chave. O `endpoint` é armazenado e verificado: mesma chave em
operação diferente é bug de cliente e retorna `422` (modelo Stripe).

A alternativa — `endpoint` na PK — permitiria reuso legítimo entre operações.
Escolhido o mais estrito.

O valor de `ENDPOINT` é constante escrita à mão. Derivar de
`getClass().getSimpleName()` ou do path HTTP faria toda retentativa em voo
devolver `422` no dia de um rename.

### 11. Retenção de 7 dias

Cobre com folga qualquer janela de retry realista, inclusive orquestrador que
ficou fora do ar e volta retomando trabalho pendente. Retentativa após o prazo
é tratada como operação nova, e isso está documentado no contrato.

O limite existe porque a tabela cresce a cada requisição e nunca é lida depois
de alguns segundos. Sem expurgo, viraria a maior tabela do banco guardando
resposta HTTP para sempre.

---

## Alternativas consideradas

### Claim em transação separada (`REQUIRES_NEW`)

Insere e commita o registro antes do caso de uso rodar, o que resolve o
problema da transação abortada em JPA puro.

**Descartada** porque cria registro órfão: crash entre o claim commitado e a
gravação da resposta deixa a chave travada por 7 dias devolvendo replay de
nada. Exigiria coluna de estado e job de expurgo de `IN_PROGRESS`. Também custa
três transações por requisição no caminho feliz.

Continua sendo a saída **necessária** se um caso de uso passar a chamar serviço
externo — ver "Consequências".

### Ler, inserir, e retentar a requisição no conflito

`findById` primeiro; se ausente, insere; se duas threads virem ausente juntas,
uma leva violação, deixa a transação morrer e o adapter HTTP repete a
requisição inteira uma vez.

**Descartada** por complexidade no adapter e latência dobrada ocasional, sem
ganho sobre `ON CONFLICT`.

### Advisory lock (`pg_advisory_xact_lock`)

Serializa por chave, tornando ler-depois-inserir seguro numa transação só.

**Descartada**: também exige query nativa, e serializa mais do que o necessário
— o `ON CONFLICT` já resolve no índice.

### `PUT` com id gerado pelo cliente

A PK de `gift_card_order` seria a própria sentinela, dispensando a tabela
auxiliar.

**Descartada** porque perde o cache da resposta (obrigaria a remontar a view),
perde a verificação de fingerprint, e não é uniforme: `:capture` não cria
recurso novo — muda estado —, então não há PK para colidir. Uma solução para
seis endpoints vale mais que seis soluções.

### Deduplicação por conteúdo

**Impossível**, e essa impossibilidade é o que motiva todo o desenho. Ver
Contexto.

---

## Consequências

### Positivas

- Retentativa devolve a mesma resposta, com o mesmo `orderId`
- Sob concorrência, o índice único garante execução única — não o código
- Sem estado intermediário: sem registro órfão, sem job de limpeza
- Cliente com bug descobre o reuso de chave imediatamente, em vez de receber
  silenciosamente os dados de outro pedido
- O mecanismo é o mesmo para os seis endpoints financeiros

### Negativas e limites

- O caso de uso conhece `status`, `body` e `location` ao repassá-los ao
  `complete`. É inevitável: a tabela **guarda uma resposta HTTP**, e a camada
  que controla a transação inevitavelmente tangencia isso. O `CreateOrderOutputBoundary`
  mantém a dependência invertida — o caso de uso não conhece Jackson nem
  `HttpStatus` — mas não elimina o acoplamento conceitual
- O controller devolve `String` em vez de DTO. Feio, e é o que garante o replay
  byte-a-byte
- A tabela cresce com cada requisição e depende de um job de expurgo que ainda
  não existe
- Cada Command precisa da sua canonização escrita à mão. Repete um pouco, e
  mantém a ordem dos campos explícita e local — reflection faria um refactor
  inocente mudar todo fingerprint gravado
- `409 IN_PROGRESS` pode ser código morto (item 8)

### O que força revisão deste ADR

**Caso de uso que chame serviço externo dentro da operação.** Não se segura
transação aberta durante chamada de rede, então o claim passaria a commitar
separado. Isso exige, em conjunto:

1. reintroduzir a coluna de estado em `idempotency_record`
2. um job de expurgo de `IN_PROGRESS` órfão, com TTL curto e separado da
   retenção de 7 dias dos completos
3. o `409 IN_PROGRESS` passa a ser leitura de coluna, e deixa de ser
   possivelmente inalcançável

O `RecordIsCompleteReplayRule` já existe no código antecipando esse cenário —
hoje ele não dispara.

**Campo novo no payload.** Entra na canonização e exige bump do prefixo de
versão. Registros da geração anterior passam a ser tratados como replay
incondicional, em vez de devolver `422` falso para retentativas legítimas em
voo.

---

## Referências

- `V2__create_idempotency_record.sql` — schema e comentários
- `SaveGiftCardOrderAdapter` — claim e ramificação do replay
- `adapters/createordergiftrcard/replay/` — regras de verificação, com `@Order`
- `Fingerprint` e `CreateOrderGiftCardOrderCommand#canonicalForm`
- `giftcard-service-openapi.yaml` — seção "Como integrar a idempotência"
- RFC 9457 — formato dos erros
