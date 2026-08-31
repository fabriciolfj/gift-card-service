-- =====================================================================
-- T-014 · FK pendente desde a V1
--
-- A coluna gift_card_order.gift_card_id foi criada na V1 sem FK porque a
-- tabela referenciada não existia ainda.
--
-- Note que a ordem de inserção na ativação resolve sozinha a aparente
-- circularidade: insere gift_card primeiro (não referencia o pedido),
-- depois atualiza gift_card_order.gift_card_id. Não é preciso constraint
-- deferrable.
-- =====================================================================

alter table gift_card_order
    add constraint fk_gco_gift_card
    foreign key (gift_card_id) references gift_card (id);

comment on constraint fk_gco_gift_card on gift_card_order is
    'Relação pedido→vale. O sentido inverso NÃO existe: gift_card não tem order_id.';
