-- =====================================================================
-- T-003 · gift_card_order
--
-- Pedido de emissão de vale-presente. Representa a INTENÇÃO de compra:
-- não gera saldo nem código utilizável (RN-07). O vale nasce apenas em
-- :activate (T-014), após confirmação do pagamento.
-- =====================================================================

create table gift_card_order (
    id                  uuid         not null,

    -- RN-04 · centavos em bigint. Nunca numeric/decimal, nunca double.
    amount_cents        bigint       not null,

    -- Mantida na persistência apesar de o request NÃO aceitar o campo:
    -- custa nada aqui e evita migration se surgir uma segunda moeda.
    -- O adapter resolve a constante na borda; o VO Money segue
    -- carregando moeda, para não refatorar o agregado depois.
    currency            char(3)      not null default 'BRL',

    status              varchar(20)  not null,

    -- RN-16 · política de validade vigente NO MOMENTO DA COMPRA.
    -- Preenchida pela APLICAÇÃO, não pelo cliente: o request não aceita
    -- este campo. A coluna registra o que o sistema DECIDIU.
    -- Congelada aqui, não resolvida na ativação: se a política default
    -- mudar entre a compra e a confirmação do pagamento, este pedido
    -- mantém a que o cliente contratou.
    expiry_policy_ref   varchar(40)  not null,

    purchaser_ref       varchar(64),

    -- Destinatário embutido, não normalizado: relação 1:1, sempre lido
    -- em conjunto, opcional, sem consulta própria. Tabela separada só
    -- adicionaria um join sem ganho.
    -- ATENÇÃO: recipient_email é PII. Nunca em log. Ver nota de purga.
    recipient_name      varchar(120),
    recipient_email     varchar(255),
    recipient_message   varchar(280),

    -- Liquidação. Preenchidos em :activate (T-014).
    payment_ref         varchar(64),
    paid_amount_cents   bigint,
    paid_at             timestamptz,

    -- FK adicionada na migration do gift_card (T-012): a tabela
    -- referenciada ainda não existe nesta versão.
    gift_card_id        uuid,

    cancel_reason       varchar(280),
    correlation_id      varchar(64),

    -- Optimistic locking. Sem uso em T-006/T-007, mas nasce agora para
    -- evitar migration futura em tabela já populada. Usado na transição
    -- de estado do :activate.
    version             bigint       not null default 0,

    created_at          timestamptz  not null default now(),
    updated_at          timestamptz  not null default now(),
    activated_at        timestamptz,
    cancelled_at        timestamptz,

    constraint pk_gift_card_order primary key (id),

    constraint ck_gco_amount_positive
        check (amount_cents > 0),

    constraint ck_gco_currency
        check (currency = 'BRL'),

    constraint ck_gco_status
        check (status in ('PENDING_PAYMENT', 'ACTIVATED', 'CANCELLED', 'EXPIRED')),

    -- Máquina de estados parcialmente garantida no banco.
    -- ACTIVATED se e somente se existe vale E existe liquidação.
    -- Torna impossível pedido ativado sem vale, ou vale associado a
    -- pedido ainda pendente, mesmo com bug no agregado.
    constraint ck_gco_activated_consistency
        check (
            (status = 'ACTIVATED') =
            (gift_card_id is not null
             and payment_ref is not null
             and activated_at is not null)
        ),

    constraint ck_gco_cancelled_consistency
        check ((status = 'CANCELLED') = (cancelled_at is not null)),

    -- Valor pago confere com o pedido. Divergência → PAYMENT_AMOUNT_MISMATCH.
    constraint ck_gco_paid_amount
        check (paid_amount_cents is null or paid_amount_cents = amount_cents)
);

-- Webhook de pagamento entregue duas vezes não ativa dois pedidos.
-- Camada de proteção INDEPENDENTE da Idempotency-Key: aqui a chave é de
-- negócio (a liquidação), lá é de transporte (a requisição do cliente).
create unique index uq_gco_payment_ref
    on gift_card_order (payment_ref)
    where payment_ref is not null;

-- Um vale pertence a exatamente um pedido.
create unique index uq_gco_gift_card
    on gift_card_order (gift_card_id)
    where gift_card_id is not null;

-- Job de cancelamento por timeout (US-11) varre pendentes antigos.
-- Índice parcial: a fração PENDING_PAYMENT é pequena e o índice fica
-- muito menor que o equivalente completo.
create index ix_gco_pending_created
    on gift_card_order (created_at)
    where status = 'PENDING_PAYMENT';

create index ix_gco_purchaser
    on gift_card_order (purchaser_ref)
    where purchaser_ref is not null;

comment on table gift_card_order is
    'Pedido de emissão de vale. Não gera saldo até a ativação (RN-07).';

comment on column gift_card_order.expiry_policy_ref is
    'Política congelada na compra, resolvida pela aplicação (RN-16).';

comment on column gift_card_order.payment_ref is
    'Referência da liquidação. Único: deduplica webhook de pagamento.';

comment on column gift_card_order.recipient_email is
    'PII. Não logar. Sujeito a purga após entrega do vale.';
