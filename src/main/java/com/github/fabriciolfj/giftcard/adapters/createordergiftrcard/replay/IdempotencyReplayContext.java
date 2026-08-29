package com.github.fabriciolfj.giftcard.adapters.createordergiftrcard.replay;

import com.github.fabriciolfj.giftcard.persistences.rows.IdempotencyRecordRow;

/**
 * Dados que uma {@link IdempotencyReplayRule} precisa para decidir se a retentativa
 * é legítima: o que este request esperava e o que está gravado no registro.
 */
public record IdempotencyReplayContext(
        String expectedEndpoint,
        String expectedFingerprint,
        IdempotencyRecordRow stored
) {
}
