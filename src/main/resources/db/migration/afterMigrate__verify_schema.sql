-- =====================================================================
-- afterMigrate · Verificação de sanidade do schema
--
-- Callback do Flyway: roda após TODA migração bem-sucedida, inclusive no
-- startup em produção. Falha aqui aborta o deploy — que é o objetivo.
--
-- Não substitui teste. Pega a classe de erro que teste não pega: alguém
-- aplicou DDL manualmente em produção e derrubou uma constraint.
--
-- SUBSTITUI o arquivo da T-006. Callbacks não têm checksum, então editar
-- é seguro — diferente de migration versionada.
-- =====================================================================

do $$
declare
    missing text;
begin
    -- Constraints que sustentam invariantes de negócio. Perder qualquer
    -- uma silenciosamente é como nasce corrupção de dado financeiro.
    select string_agg(expected, ', ')
      into missing
      from (values
              -- T-006
              ('ck_gco_activated_consistency'),
              ('ck_gco_cancelled_consistency'),
              ('ck_gco_paid_amount'),
              ('ck_gco_amount_positive'),
              ('pk_idempotency_record'),
              -- T-014
              ('ck_gc_status'),
              ('ck_gc_block_reason'),
              ('ck_ledger_amount_positive'),
              ('ck_ledger_balance_non_negative'),
              ('ck_ledger_adjustment_reason'),
              ('ck_snapshot_non_negative'),
              ('ck_snapshot_held_within_balance'),
              ('fk_gco_gift_card'),
              ('fk_ledger_gift_card')
           ) as t(expected)
     where not exists (
             select 1 from pg_constraint where conname = t.expected
           );

    if missing is not null then
        raise exception 'Constraints ausentes: %', missing;
    end if;

    -- Índices únicos que garantem deduplicação e corretude contábil.
    select string_agg(expected, ', ')
      into missing
      from (values
              -- T-006
              ('uq_gco_payment_ref'),
              ('uq_gco_gift_card'),
              -- T-014 · os dois abaixo tornam double-capture e
              -- double-expiry impossíveis no banco
              ('uq_gc_code_hash'),
              ('uq_ledger_capture_per_hold'),
              ('uq_ledger_expiry_per_card')
           ) as t(expected)
     where not exists (
             select 1 from pg_indexes where indexname = t.expected
           );

    if missing is not null then
        raise exception 'Índices ausentes: %', missing;
    end if;

    -- Triggers de updated_at.
    select string_agg(expected, ', ')
      into missing
      from (values
              ('trg_gift_card_order_updated_at'),
              ('trg_gift_card_updated_at'),
              ('trg_balance_snapshot_updated_at')
           ) as t(expected)
     where not exists (
             select 1 from pg_trigger where tgname = t.expected
           );

    if missing is not null then
        raise exception 'Triggers ausentes: %', missing;
    end if;

    -- RN-02 · ledger_entry não deve ter coluna de atualização: linha de
    -- ledger não muda. Se alguém adicionar, é sinal de que o append-only
    -- foi mal compreendido.
    if exists (
        select 1 from information_schema.columns
         where table_name = 'ledger_entry' and column_name = 'updated_at'
    ) then
        raise exception 'ledger_entry.updated_at não deveria existir: o ledger é append-only (RN-02)';
    end if;

    raise notice 'Schema verificado.';
end;
$$;
