-- =====================================================================
-- T-014 · gift_card
--
-- O vale propriamente dito. Só existe após a confirmação do pagamento
-- (RN-07) — antes disso há apenas o pedido, em gift_card_order.
--
-- MODELAGEM DA RELAÇÃO: esta tabela NÃO tem order_id.
-- A V1 já guarda gift_card_order.gift_card_id, com índice único, e a
-- constraint ck_gco_activated_consistency depende dele. Duplicar a
-- relação nos dois sentidos criaria duas FKs apontando uma para a outra,
-- com a mesma informação em dois lugares e a possibilidade de
-- divergirem. A consulta inversa ("qual pedido gerou este vale") sai
-- pelo índice uq_gco_gift_card.
-- =====================================================================

create table gift_card (
    id                 uuid         not null,

    -- RN-08 · HMAC-SHA-256 com pepper, hex (64 chars).
    -- HMAC e não SHA-256 puro: aqui existe adversário. Um dump do banco
    -- sem o pepper (que vive fora, em variável de ambiente) não permite
    -- força bruta offline.
    -- O código em claro NUNCA é persistido, nem em log, nem em evento.
    code_hash          char(64)     not null,

    -- Últimos 4 caracteres, para o atendimento identificar o vale sem
    -- expor o resto.
    code_last4         char(4)      not null,

    status             varchar(20)  not null,

    currency           char(3)      not null default 'BRL',

    -- RN-16 · calculado na ativação como activated_at + duração da
    -- política CONGELADA NO PEDIDO, não a vigente hoje.
    expires_at         timestamptz  not null,

    -- Preenchido quando status = BLOCKED. Auditoria exige motivo.
    block_reason       varchar(280),

    -- Optimistic locking. Sem uso nesta task; a US-05 (hold) precisa, e
    -- criar agora evita migration em tabela populada.
    version            bigint       not null default 0,

    created_at         timestamptz  not null default now(),
    updated_at         timestamptz  not null default now(),

    constraint pk_gift_card primary key (id),

    constraint ck_gc_status
        check (status in ('ACTIVE', 'PARTIALLY_USED', 'DEPLETED',
                          'EXPIRED', 'BLOCKED', 'CANCELLED')),

    constraint ck_gc_currency
        check (currency = 'BRL'),

    -- Bloqueio sem motivo registrado é inauditável.
    constraint ck_gc_block_reason
        check ((status = 'BLOCKED') = (block_reason is not null)),

    constraint ck_gc_code_last4_alphabet
        check (code_last4 ~ '^[0-9A-HJKMNP-TV-Z]{4}$')
);

-- Colisão de código é praticamente impossível, mas o índice é o que a
-- torna impossível DE FATO. O gerador captura a violação e regera, com
-- teto de tentativas para não mascarar um CSPRNG quebrado.
create unique index uq_gc_code_hash
    on gift_card (code_hash);

-- Job mensal de expiração (US-12) varre vales vencidos ainda ativos.
-- Índice parcial: a fração relevante é pequena.
create index ix_gc_expires_active
    on gift_card (expires_at)
    where status in ('ACTIVE', 'PARTIALLY_USED');

create index ix_gc_code_last4
    on gift_card (code_last4);

-- Reaproveita a função criada na V3.
create trigger trg_gift_card_updated_at
    before update on gift_card
    for each row
    execute function set_updated_at();

comment on table gift_card is
    'Vale-presente. Nasce apenas na ativação, após o pagamento (RN-07).';

comment on column gift_card.code_hash is
    'HMAC-SHA-256 com pepper externo. O código em claro nunca é persistido.';

comment on column gift_card.expires_at is
    'activated_at + duração da política congelada no pedido (RN-16).';
