package com.github.fabriciolfj.giftcard.configurations;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.convert.PeriodUnit;

import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.util.Map;

@ConfigurationProperties("giftcard.expiry")
public record ExpiryProperties(
        String defaultPolicyRef,
        Map<String, Policy> policies
) {
    public record Policy(@PeriodUnit(ChronoUnit.MONTHS) Period months) { }
}