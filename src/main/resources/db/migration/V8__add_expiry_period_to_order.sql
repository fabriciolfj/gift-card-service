-- =====================================================================
-- T-014 · duração congelada · DECISÃO PENDENTE 8
--
-- Aplique esta migration APENAS se a decisão for congelar também a
-- duração, e não só a referência da política.
--
-- O PROBLEMA QUE ELA RESOLVE
--
-- Hoje o pedido guarda apenas expiry_policy_ref ("STANDARD_12M"). Se
-- alguém EDITAR essa entrada no application.yaml de P12M para P6M, o
-- congelamento não protege: a referência continua a mesma, mas mudou de
-- significado. O pedido comprado sob 12 meses passaria a resolver 6.
--
-- O congelamento por referência só funciona sob a convenção "políticas
-- são imutáveis; mudança cria entrada nova" — e convenção quebra.
--
-- Há ainda um segundo caso: política aposentada some do YAML, e a
-- ativação de um pedido antigo passa a falhar por não encontrá-la.
--
-- O QUE VOCÊ GANHA
--
-- Com a duração gravada, o cálculo de expires_at na ativação não depende
-- da configuração atual. Edição do YAML não afeta pedido já criado.
--
-- Mantemos as DUAS colunas: expiry_policy_ref para auditoria (saber qual
-- política se aplicou, útil em disputa) e expiry_period para o cálculo.
-- =====================================================================

alter table gift_card_order
    add column expiry_period varchar(20);

-- Retroativo para os pedidos já criados. Ajuste o valor se a política
-- default vigente na criação deles era outra.
update gift_card_order
   set expiry_period = 'P12M'
 where expiry_period is null;

alter table gift_card_order
    alter column expiry_period set not null;

comment on column gift_card_order.expiry_period is
    'Duração ISO-8601 congelada na compra (ex.: P12M). Torna o cálculo de expires_at independente da configuração atual.';
