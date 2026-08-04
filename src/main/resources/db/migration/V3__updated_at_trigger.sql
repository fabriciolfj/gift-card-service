-- =====================================================================
-- V3 · Trigger de updated_at
--
-- O `default now()` da coluna só vale no INSERT. Sem trigger, updated_at
-- congela no momento da criação e mente pelo resto da vida da linha.
--
-- Trigger em vez de @UpdateTimestamp do Hibernate porque protege também
-- UPDATE manual em produção — que é justamente quando você mais quer
-- saber a hora em que a linha mudou.
-- =====================================================================

create or replace function set_updated_at()
returns trigger
language plpgsql
as $$
begin
    new.updated_at = now();
    return new;
end;
$$;

create trigger trg_gift_card_order_updated_at
    before update on gift_card_order
    for each row
    execute function set_updated_at();

comment on function set_updated_at() is
    'Mantém updated_at em qualquer UPDATE, inclusive fora da aplicação.';
