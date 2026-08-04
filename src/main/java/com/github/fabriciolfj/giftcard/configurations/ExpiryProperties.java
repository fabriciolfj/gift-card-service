package com.github.fabriciolfj.giftcard.configurations;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

@ConfigurationProperties("giftcard.expiry")
public record ExpiryProperties(String defaultPolicyRef,
                               Map<String, String> policies) {
}