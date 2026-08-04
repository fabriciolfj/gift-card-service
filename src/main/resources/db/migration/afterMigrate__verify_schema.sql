-- =====================================================================
-- afterMigrate · Verificação de sanidade do schema
--
-- Callback do Flyway: roda após TODA migração bem-sucedida, inclusive no
-- startup em produção. Falha aqui aborta o deploy — que é o objetivo.
--
-- Não substitui teste. Pega a classe de erro que teste não pega: alguém
-- aplicou DDL manualmente em produção e derrubou uma constraint.
-- =====================================================================

do $$
declare
    missing text;
begin
    -- As constraints que sustentam invariantes de negócio.
    -- Perder qualquer uma delas silenciosamente é como nasce corrupção
    -- de dado financeiro.
    select string_agg(expected, ', ')
      into missing
      from (values
              ('ck_gco_activated_consistency'),
              ('ck_gco_cancelled_consistency'),
              ('ck_gco_paid_amount'),
              ('ck_gco_amount_positive'),
              ('pk_idempotency_record')
           ) as t(expected)
     where not exists (
             select 1 from pg_constraint where conname = t.expected
           );

    if missing is not null then
        raise exception 'Constraints ausentes: %', missing;
    end if;

    -- Índices únicos que garantem deduplicação.
    select string_agg(expected, ', ')
      into missing
      from (values
              ('uq_gco_payment_ref'),
              ('uq_gco_gift_card')
           ) as t(expected)
     where not exists (
             select 1 from pg_indexes where indexname = t.expected
           );

    if missing is not null then
        raise exception 'Índices ausentes: %', missing;
    end if;

    -- O trigger de updated_at.
    if not exists (
        select 1 from pg_trigger
         where tgname = 'trg_gift_card_order_updated_at'
    ) then
        raise exception 'Trigger trg_gift_card_order_updated_at ausente';
    end if;

    raise notice 'Schema verificado.';
end;
$$;
