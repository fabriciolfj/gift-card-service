-- Undo da V5.
--
-- ATENÇÃO: destrói o LEDGER — o registro contábil de todo o dinheiro em
-- circulação. Não é reconstruível a partir de nenhuma outra tabela.
-- Só executar em ambiente descartável.
drop table if exists ledger_entry;
