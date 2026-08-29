package com.github.fabriciolfj.giftcard.configurations;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@ConfigurationProperties("giftcard.idempotency")
public record IdempotencyProperties(
        Duration retention,
        Integer keyMinLength,
        Integer keyMaxLength
) {
}
