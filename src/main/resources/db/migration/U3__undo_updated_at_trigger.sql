-- Undo da V3. Flyway Teams; em Community serve como script manual de
-- rollback, e é o que faz a migration atender ao DoD "reversível".
drop trigger if exists trg_gift_card_order_updated_at on gift_card_order;
drop function if exists set_updated_at();
