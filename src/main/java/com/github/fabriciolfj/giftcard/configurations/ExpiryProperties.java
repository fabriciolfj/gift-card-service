package com.github.fabriciolfj.giftcard.configurations;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Period;
import java.util.Map;

@ConfigurationProperties("giftcard.expiry")
public record ExpiryProperties(
        String defaultPolicyRef,
        Map<String, Policy> policies
) {
    public record Policy(Period duration) { }
}