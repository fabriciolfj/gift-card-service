package com.github.fabriciolfj.giftcard.persistences.repositories;

import com.github.fabriciolfj.giftcard.persistences.rows.IdempotencyRecordRow;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class IdempotencyRepository {

    private static final String SQL_LOAD = """
            select endpoint, request_fingerprint,
                   response_status, response_body, response_location, aggregate_id
              from idempotency_record
             where idempotency_key = :key
            """;

    private static final String SQL_INSERT = """
            insert into idempotency_record
                (idempotency_key, endpoint, request_fingerprint,
                 correlation_id, created_at, expires_at)
            values
                (:key, :endpoint, :fingerprint,
                 :correlationId, now(), now() + interval '7 days')
            on conflict (idempotency_key) do nothing            
            """;

    private final JdbcClient jdbcClient;

    private IdempotencyRepository(final JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    public Optional<IdempotencyRecordRow> load(final String key) {
        return jdbcClient.sql(SQL_LOAD)
                .param("key", key)
                .query(new IdempotrencyRowMapper())
                .optional();
    }

    public boolean tryClaim(final String fingerprint, final String key, final String correlationId, final String endpoint) {
        int rows = jdbcClient.sql(SQL_INSERT)
                .param("key", key)
                .param("endpoint", endpoint)
                .param("fingerprint", fingerprint)
                .param("correlationId", correlationId)
                .update();

        return rows == 1;
    }
}
