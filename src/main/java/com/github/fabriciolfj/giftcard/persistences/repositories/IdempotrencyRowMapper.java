package com.github.fabriciolfj.giftcard.persistences.repositories;

import com.github.fabriciolfj.giftcard.persistences.rows.IdempotencyRecordRow;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class IdempotrencyRowMapper implements RowMapper<IdempotencyRecordRow> {

    @Override
    public IdempotencyRecordRow mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new IdempotencyRecordRow(
                rs.getString("endpoint"),
                rs.getString("request_fingerprint"),
                rs.getInt("response_status"),
                rs.getString("response_body"),
                rs.getString("response_location"),
                rs.getObject("aggregate_id", UUID.class)
        );
    }
}
