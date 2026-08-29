package com.github.fabriciolfj.giftcard.usecases.createordergift;

import com.github.fabriciolfj.giftcard.domain.ExpiryPolicy;

public interface GetParametersExpiryGateway {

    ExpiryPolicy process();
}
