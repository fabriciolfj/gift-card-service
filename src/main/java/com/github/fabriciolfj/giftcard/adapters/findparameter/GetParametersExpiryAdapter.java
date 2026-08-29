package com.github.fabriciolfj.giftcard.adapters.findparameter;

import com.github.fabriciolfj.giftcard.configurations.ExpiryProperties;
import com.github.fabriciolfj.giftcard.domain.ExpiryPolicy;
import com.github.fabriciolfj.giftcard.usecases.common.ExpiryPolicyMapper;
import com.github.fabriciolfj.giftcard.usecases.createordergift.GetParametersExpiryGateway;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GetParametersExpiryAdapter implements GetParametersExpiryGateway {

    private final ExpiryProperties expiryProperties;

    @PostConstruct
    void validateDefaultPolicy() {
        final var ref = expiryProperties.defaultPolicyRef();
        final var policies = expiryProperties.policies();

        if (policies == null || !policies.containsKey(ref)) {
            throw new IllegalStateException(
                    "giftcard.expiry.default-policy-ref aponta para política inexistente: '%s'. Disponíveis: %s"
                            .formatted(ref, policies == null ? "(nenhuma)" : policies.keySet()));
        }

        if (policies.get(ref).months() == null) {
            throw new IllegalStateException(
                    "Política '%s' não tem duração configurada".formatted(ref));
        }
    }

    @Override
    public ExpiryPolicy process() {
        final var ref = expiryProperties.defaultPolicyRef();
        final var policy = expiryProperties.policies();

        return ExpiryPolicyMapper.of(ref, policy);
    }
}
