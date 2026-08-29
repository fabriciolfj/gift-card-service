package com.github.fabriciolfj.giftcard.usecases.common;

import com.github.fabriciolfj.giftcard.configurations.ExpiryProperties;
import com.github.fabriciolfj.giftcard.domain.ExpiryPolicy;

import java.util.Map;

public class ExpiryPolicyMapper {

    private ExpiryPolicyMapper() { }

    public static ExpiryPolicy of(final String ref, final Map<String, ExpiryProperties.Policy> properties) {
        final var policy = properties.get(ref);

        return new ExpiryPolicy(ref, policy != null ? policy.months() : null);
    }
}
