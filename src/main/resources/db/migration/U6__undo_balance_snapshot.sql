-- Undo da V6. Não destrói dado financeiro: o snapshot é cache e pode ser
-- reconstruído a partir do ledger.
drop trigger if exists trg_balance_snapshot_updated_at on balance_snapshot;
drop table if exists balance_snapshot;
