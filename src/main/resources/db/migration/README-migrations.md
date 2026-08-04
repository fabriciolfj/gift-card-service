# Migrations · giftcard-service

## Layout

```
src/main/resources/db/migration/
├── V1__create_gift_card_order.sql
├── V2__create_idempotency_record.sql
├── V3__updated_at_trigger.sql
├── U1__undo_gift_card_order.sql
├── U2__undo_idempotency_record.sql
├── U3__undo_updated_at_trigger.sql
└── afterMigrate__verify_schema.sql
```

`V1` e `V2` são independentes entre si. `V3` depende de `V1`. Os `U*`
executam na ordem inversa.

## Configuração

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/giftcard
    username: giftcard
    password: ${DB_PASSWORD}
  jpa:
    hibernate:
      ddl-auto: validate      # NUNCA update ou create. Flyway é a fonte da verdade.
    open-in-view: false
  flyway:
    enabled: true
    locations: classpath:db/migration
    baseline-on-migrate: false
    validate-on-migrate: true
    clean-disabled: true      # impede flyway:clean em produção
```

### Por que `ddl-auto: validate`

Com `update`, o Hibernate e o Flyway disputam a autoria do schema e vencem
alternadamente — você acaba com produção divergindo de qualquer migration
já escrita. `validate` faz o startup falhar se as entidades não baterem
com o schema migrado, que é o comportamento desejado.

### Por que `clean-disabled: true`

`flyway:clean` dropa tudo. É o default do Flyway desde a versão 9, e vale
manter explícito para quem ler a configuração.

## Convenções

**Nunca edite uma migration já aplicada.** O Flyway guarda o checksum em
`flyway_schema_history`; alterar o arquivo faz o `validate` quebrar no
próximo startup. Correção é sempre `V{n+1}`.

**Uma migration por unidade lógica.** `V1` e `V2` criam tabelas
independentes e por isso são separadas, embora entrem juntas na mesma
task (T-003).

**DDL do PostgreSQL é transacional.** Um `create table` que falha na
metade não deixa resto — diferente de MySQL e Oracle. Isso simplifica
recuperação de migration com erro.

## Sobre os `U*`

Undo é recurso do Flyway Teams. Em Community os arquivos não executam
automaticamente, e servem como:

1. script manual de rollback, aplicável com `psql`;
2. documentação do que a migration criou;
3. cumprimento do DoD "migrations reversíveis" da T-006/T-007.

**U2 destrói o histórico de idempotência.** Retentativas em voo passariam
a ser tratadas como operações novas — em endpoint financeiro, cobrança
duplicada. Só execute com tráfego drenado.

## Sobre o `afterMigrate`

Roda após toda migração bem-sucedida, inclusive no startup em produção.
Verifica que as constraints de invariante e os índices de deduplicação
existem, e aborta o deploy se faltar algum.

Não substitui teste. Pega a classe de erro que teste não pega: DDL
aplicado manualmente em produção derrubando uma constraint.

## Teste com Testcontainers

```java
@Testcontainers
@SpringBootTest
class MigrationTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres =
        new PostgreSQLContainer<>("postgres:17-alpine");

    @Test
    void schemaAplicaLimpo() { /* o contexto subir já valida */ }

    @Test
    void constraintDeConsistenciaImpedeAtivacaoSemVale() {
        // insere pedido ACTIVATED sem gift_card_id
        // espera violação de ck_gco_activated_consistency
    }

    @Test
    void triggerAtualizaUpdatedAt() {
        // insere, lê updated_at, faz UPDATE, relê
        // espera valor maior
    }
}
```

**Use Postgres real, não H2.** O teste de concorrência da T-006 depende da
semântica de lock no índice único, e o H2 não a reproduz — passaria verde
sem ter testado nada. Fixe a versão da imagem: `latest` faz o teste mudar
de comportamento sem ninguém mexer no código.

## Nota sobre `search_path`

As migrations não qualificam schema, então caem no `search_path` da
conexão — normalmente `public`. Se você for isolar por schema, defina
`spring.flyway.schemas` e `default-schema` em vez de qualificar tabela por
tabela nos arquivos.
