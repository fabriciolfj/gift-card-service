package com.github.fabriciolfj.giftcard.usecases.creategiftcard;

import com.github.fabriciolfj.giftcard.domain.GiftCardOrder;

public interface ActivateGiftorderGateway {

    void process(final GiftCardOrder order);
}
