package com.github.fabriciolfj.giftcard.adapters.findparameter;

import com.github.fabriciolfj.giftcard.configurations.ExpiryProperties;
import com.github.fabriciolfj.giftcard.domain.ExpiryPolicy;
import com.github.fabriciolfj.giftcard.usecases.common.ExpiryPolicyMapper;
import com.github.fabriciolfj.giftcard.usecases.createordergift.GetParametersExpiryGateway;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GetParametersExpiryAdapter implements GetParametersExpiryGateway {

    private final ExpiryProperties expiryProperties;

    @Override
    public ExpiryPolicy process() {
        final var ref = expiryProperties.defaultPolicyRef();
        final var policy = expiryProperties.policies();

        return ExpiryPolicyMapper.of(ref, policy);
    }
}
