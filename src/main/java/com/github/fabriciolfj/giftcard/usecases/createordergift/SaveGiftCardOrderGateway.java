package com.github.fabriciolfj.giftcard.usecases.createordergift;

import com.github.fabriciolfj.giftcard.command.IdempotentCommand;
import com.github.fabriciolfj.giftcard.domain.GiftCardOrder;

public interface SaveGiftCardOrderGateway {

    GiftCardOrder execute(GiftCardOrder giftCardOrder, IdempotentCommand idempotentCommand, String key);
}
