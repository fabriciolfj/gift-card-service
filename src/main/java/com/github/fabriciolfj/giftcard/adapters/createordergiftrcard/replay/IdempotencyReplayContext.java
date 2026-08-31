package com.github.fabriciolfj.giftcard.adapters.createordergiftrcard.replay;

import com.github.fabriciolfj.giftcard.persistences.rows.IdempotencyRecordRow;


public record IdempotencyReplayContext(
        String expectedEndpoint,
        String expectedFingerprint,
        IdempotencyRecordRow stored
) {
}
