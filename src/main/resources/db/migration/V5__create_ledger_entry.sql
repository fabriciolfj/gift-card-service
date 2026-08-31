-- =====================================================================
-- T-014 · ledger_entry
--
-- RN-02 · APPEND-ONLY. Erro não se corrige com UPDATE nem DELETE, e sim
-- com lançamento compensatório do tipo ADJUSTMENT, com motivo e operador.
-- Não deve existir nenhum caminho de código que edite uma linha daqui.
--
-- RN-03 · saldo_contábil = Σ lançamentos. O balance_snapshot é cache e
-- precisa ser sempre reconstruível a partir do zero — há teste para isso.
--
-- RN-05 · HOLD e RELEASE NÃO são lançamentos: eles não movem saldo
-- contábil, apenas reservam disponível. Vivem na tabela de autorizações
-- (US-05). Por isso não aparecem no check de tipo abaixo.
-- =====================================================================

create table ledger_entry (
    id                   uuid         not null,
    gift_card_id         uuid         not null,

    type                 varchar(20)  not null,

    -- Sempre POSITIVO. O sinal é derivado do tipo — ISSUE e REFUND
    -- creditam, CAPTURE e EXPIRY debitam. Guardar valor com sinal
    -- convidaria a somar sem olhar o tipo.
    amount_cents         bigint       not null,

    -- Saldo do vale APÓS este lançamento. Redundante com a soma, e é
    -- justamente o que permite auditar: se a soma dos lançamentos não
    -- reproduzir esta coluna, algo foi gravado fora de ordem.
    balance_after_cents  bigint       not null,

    -- Presente em CAPTURE (a autorização de origem). Base do índice
    -- parcial único abaixo.
    hold_id              uuid,

    external_ref         varchar(64),

    -- Obrigatório em ADJUSTMENT: correção manual sem motivo é
    -- inauditável.
    reason               varchar(280),

    -- Quem originou. Preenchido em ADJUSTMENT e em ações de operador.
    actor                varchar(64),

    correlation_id       varchar(64),

    created_at           timestamptz  not null default now(),

    constraint pk_ledger_entry primary key (id),

    constraint fk_ledger_gift_card
        foreign key (gift_card_id) references gift_card (id),

    constraint ck_ledger_type
        check (type in ('ISSUE', 'CAPTURE', 'REFUND', 'EXPIRY', 'ADJUSTMENT')),

    constraint ck_ledger_amount_positive
        check (amount_cents > 0),

    -- RN-01 · nenhuma operação pode deixar o saldo negativo.
    constraint ck_ledger_balance_non_negative
        check (balance_after_cents >= 0),

    constraint ck_ledger_adjustment_reason
        check (type <> 'ADJUSTMENT' or (reason is not null and actor is not null)),

    constraint ck_ledger_capture_has_hold
        check (type <> 'CAPTURE' or hold_id is not null)
);

-- ─────────────────────────────────────────────────────────────────────
-- OS DOIS ÍNDICES ABAIXO SÃO O CORAÇÃO DA CORRETUDE DO SERVIÇO.
--
-- Eles tornam double-capture e double-expiry IMPOSSÍVEIS NO BANCO,
-- independentemente de bug na camada de serviço. Sob concorrência, duas
-- threads atravessam qualquer `if` juntas; nenhuma atravessa um índice
-- único.
--
-- Criados nesta task apesar de CAPTURE e EXPIRY ainda não existirem:
-- agora é de graça, depois é migration com validação em tabela cheia.
-- ─────────────────────────────────────────────────────────────────────

-- RN-11 · um hold é capturável UMA ÚNICA VEZ.
create unique index uq_ledger_capture_per_hold
    on ledger_entry (hold_id)
    where type = 'CAPTURE';

-- RN-18 · expirar é idempotente: rodar o job duas vezes no mesmo dia não
-- gera dois lançamentos.
create unique index uq_ledger_expiry_per_card
    on ledger_entry (gift_card_id)
    where type = 'EXPIRY';

-- Extrato paginado por cursor (US-04), ordem cronológica decrescente.
create index ix_ledger_card_created
    on ledger_entry (gift_card_id, created_at desc, id desc);

comment on table ledger_entry is
    'RN-02 · Append-only. Correção é lançamento ADJUSTMENT, nunca UPDATE.';

comment on column ledger_entry.amount_cents is
    'Sempre positivo. O sinal vem do tipo (RN-05).';

comment on column ledger_entry.balance_after_cents is
    'Permite auditar: a soma dos lançamentos deve reproduzir esta coluna.';

-- Sem trigger de updated_at: a tabela não tem essa coluna de propósito.
-- Linha de ledger não muda.
--
-- Considere revogar a permissão para o usuário da aplicação, tornando o
-- append-only uma garantia e não uma convenção:
--
--   revoke update, delete on ledger_entry from <usuario_da_aplicacao>;
