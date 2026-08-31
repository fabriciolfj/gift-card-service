-- Undo da V4. Executar depois de U5 e U6 (dependem desta tabela) e de U7
-- (a FK do pedido).
--
-- ATENÇÃO: destrói os vales. Os códigos NÃO podem ser regerados — o
-- serviço guarda apenas o hash, e o código em claro só existiu na
-- resposta da ativação.
drop trigger if exists trg_gift_card_updated_at on gift_card;
drop table if exists gift_card;
