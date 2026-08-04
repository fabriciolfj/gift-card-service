-- Undo da V2.
-- ATENÇÃO: destrói o histórico de idempotência. Retentativas em voo
-- passarão a ser tratadas como operações novas — em endpoint financeiro
-- isso significa cobrança duplicada. Só executar com tráfego drenado.
drop table if exists idempotency_record;
