package com.github.fabriciolfj.giftcard.persistences.repositories;

import com.github.fabriciolfj.giftcard.persistences.rows.IdempotencyRecordRow;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.Optional;
import java.util.UUID;

@Repository
public class IdempotencyRepository {

    private static final String SQL_LOAD = """
            select endpoint, request_fingerprint,
                   response_status, response_body, response_location, aggregate_id
              from idempotency_record
             where idempotency_key = :key
            """;

    private static final String SQL_CLAIM = """
            insert into idempotency_record
                (idempotency_key, endpoint, request_fingerprint,
                 correlation_id, created_at, expires_at)
            values
                (:key, :endpoint, :fingerprint,
                 :correlationId, now(), now() + :retention::interval)
            on conflict (idempotency_key) do nothing
            """;

    private static final String SQL_COMPLETE = """
            update idempotency_record
               set response_status   = :status,
                   response_body     = :body,
                   response_location = :location,
                   aggregate_id      = :aggregateId
             where idempotency_key = :key
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

    /**
     * INSERT ... ON CONFLICT DO NOTHING: a tentativa de inserir É a verificação.
     * Retorna 1 se esta chamada reservou a chave, 0 se ela já existia.
     */
    public boolean tryClaim(final String key,
                            final String endpoint,
                            final String fingerprint,
                            final String correlationId,
                            final Duration retention) {
        final int rows = jdbcClient.sql(SQL_CLAIM)
                .param("key", key)
                .param("endpoint", endpoint)
                .param("fingerprint", fingerprint)
                .param("correlationId", correlationId)
                .param("retention", retention.toSeconds() + " seconds")
                .update();

        return rows == 1;
    }

    public void complete(final String key,
                         final int status,
                         final String body,
                         final String location,
                         final UUID aggregateId) {
        jdbcClient.sql(SQL_COMPLETE)
                .param("key", key)
                .param("status", status)
                .param("body", body)
                .param("location", location)
                .param("aggregateId", aggregateId)
                .update();
    }
}
