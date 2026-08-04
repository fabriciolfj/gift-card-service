-- =====================================================================
-- T-006 · idempotency_record
--
-- RN-19 · Suporte à Idempotency-Key obrigatória em POST que move valor.
--
-- Duas funções:
--
--   1. SENTINELA — a PK é o que fisicamente impede a segunda execução.
--      Não é o `if` no código: sob concorrência duas threads passam pelo
--      `if` juntas, nenhuma passa pelo índice único.
--
--   2. CACHE DA RESPOSTA — a retentativa recebe a MESMA resposta, com o
--      mesmo orderId. Devolver erro deixaria o cliente preso: ele não
--      saberia o id do pedido que ele próprio criou.
--
-- Desenho de TRANSAÇÃO ÚNICA: o claim e o caso de uso commitam juntos.
-- Não há coluna de estado, porque em MVCC a linha não commitada é
-- invisível para outras transações — nunca haveria quem a lesse. Sem
-- estado intermediário também não existe registro órfão, e portanto não
-- é preciso job de limpeza de "em processamento". Ver ADR-001.
--
-- Migrar para multi-transação (claim em REQUIRES_NEW, necessário se o
-- caso de uso passar a chamar serviço externo) exige reintroduzir a
-- coluna de estado E um job de expurgo de órfãos.
-- =====================================================================

create table idempotency_record (
    idempotency_key      varchar(64)  not null,

    -- Armazenado e VALIDADO, mas fora da chave: mesma key em endpoint
    -- diferente é bug de cliente e retorna 422 (modelo Stripe).
    endpoint             varchar(80)  not null,

    -- SHA-256 hex do command canonizado (64 chars).
    -- Não usar hash do body cru: cliente que reserializa um Map manda as
    -- chaves em outra ordem e levaria 422 sem ter mudado nada.
    -- Não usar Objects.hash nem record::hashCode: estáveis dentro de uma
    -- JVM, não entre versões — o hash gravado hoje precisa bater amanhã.
    -- Não incluir correlationId nem timestamps do cliente: mudam
    -- legitimamente entre tentativas e causariam 422 em toda retentativa.
    request_fingerprint  char(64)     not null,

    -- Resposta original, preenchida no fim da MESMA transação.
    -- Nuláveis por necessidade: o claim acontece antes do caso de uso
    -- rodar. Mas nunca são observadas nulas de fora — o commit é atômico,
    -- então de qualquer outra transação a linha só aparece completa.
    -- É esta propriedade que dispensa a coluna de estado.
    -- text e NÃO jsonb: jsonb descarta ordem de chaves e normaliza
    -- espaço, quebrando o replay byte-a-byte.
    response_status      int,
    response_body        text,
    response_location    varchar(255),

    -- Ligação com o agregado criado. O sentido é este: o pedido NÃO
    -- conhece a idempotency_key. Evita parsear JSON em query de suporte.
    aggregate_id         uuid,
    correlation_id       varchar(64),

    created_at           timestamptz  not null default now(),

    -- Retenção de 7 dias. Job de expurgo é task de JOB, fora de T-006.
    expires_at           timestamptz  not null,

    constraint pk_idempotency_record primary key (idempotency_key),

    -- Espelha a validação do contrato (minLength 16, maxLength 64).
    constraint ck_idem_key_length
        check (length(idempotency_key) between 16 and 64),

    constraint ck_idem_status_range
        check (response_status is null or response_status between 100 and 599)
);

create index ix_idem_expires
    on idempotency_record (expires_at);

comment on table idempotency_record is
    'RN-19 · Sentinela de execução única + cache da resposta original.';

comment on column idempotency_record.request_fingerprint is
    'SHA-256 do command canonizado. Detecta reuso de key com payload distinto.';

comment on column idempotency_record.response_body is
    'text e não jsonb: replay devolve o corpo byte-a-byte idêntico.';

comment on column idempotency_record.aggregate_id is
    'Id do agregado criado. O agregado não conhece a idempotency_key.';
