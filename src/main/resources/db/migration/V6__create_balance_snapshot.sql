-- =====================================================================
-- T-014 · balance_snapshot
--
-- RN-03 · CACHE do saldo, não a fonte da verdade. A fonte é o ledger.
-- Precisa ser sempre reconstruível a partir do zero — existe teste que
-- reprocessa todos os lançamentos e compara com esta tabela.
--
-- Existe para não somar dez mil linhas a cada consulta de saldo.
--
-- Atualizado na MESMA TRANSAÇÃO do lançamento (síncrono). É mais simples
-- e evita saldo obsoleto no checkout; projeção assíncrona escala melhor
-- e fica para quando doer. Ver decisão 2 do refinamento.
-- =====================================================================

create table balance_snapshot (
    gift_card_id   uuid         not null,

    -- Σ lançamentos do ledger.
    balance_cents  bigint       not null,

    -- Σ holds ativos. Sem uso nesta task; a US-05 preenche.
    held_cents     bigint       not null default 0,

    -- Último lançamento refletido. Permite detectar snapshot atrasado e
    -- reconstruir incrementalmente em vez de do zero.
    last_entry_id  uuid,

    updated_at     timestamptz  not null default now(),

    constraint pk_balance_snapshot primary key (gift_card_id),

    constraint fk_snapshot_gift_card
        foreign key (gift_card_id) references gift_card (id),

    constraint fk_snapshot_last_entry
        foreign key (last_entry_id) references ledger_entry (id),

    -- RN-01 · a invariante central, garantida no banco.
    constraint ck_snapshot_non_negative
        check (balance_cents >= 0 and held_cents >= 0),

    -- Disponível = contábil − holds. Nunca negativo.
    constraint ck_snapshot_held_within_balance
        check (held_cents <= balance_cents)
);

create trigger trg_balance_snapshot_updated_at
    before update on balance_snapshot
    for each row
    execute function set_updated_at();

comment on table balance_snapshot is
    'RN-03 · Cache do saldo. Fonte da verdade é ledger_entry.';

comment on column balance_snapshot.held_cents is
    'Σ holds ativos. Saldo disponível = balance_cents − held_cents (RN-01).';
