package com.github.fabriciolfj.giftcard.persistences.rows;

import java.util.UUID;

public record IdempotencyRecordRow(
        String endpoint,
        String requestFingerprint,
        Integer responseStatus,
        String responseBody,
        String responseLocation,
        UUID aggregateId
) {

    public boolean isComplete() {
        return responseStatus != null && responseBody != null;
    }
}