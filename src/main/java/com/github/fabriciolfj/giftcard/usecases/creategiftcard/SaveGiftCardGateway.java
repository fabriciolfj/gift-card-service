package com.github.fabriciolfj.giftcard.usecases.creategiftcard;

import com.github.fabriciolfj.giftcard.domain.GiftCard;

public interface SaveGiftCardGateway {

    void execute(final GiftCard giftCard);
}
