package com.github.fabriciolfj.giftcard.configurations;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("giftcard.order")
public record GiftCardOrderProperties(Long minAmountCents,
                                      Long maxAmountCents,
                                      Long amountMultipleCents) {
}
