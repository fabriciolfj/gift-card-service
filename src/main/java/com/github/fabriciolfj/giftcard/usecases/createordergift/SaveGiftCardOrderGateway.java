package com.github.fabriciolfj.giftcard.usecases.createordergift;

import com.github.fabriciolfj.giftcard.command.IdempotentCommand;
import com.github.fabriciolfj.giftcard.domain.GiftCardOrder;
import com.github.fabriciolfj.giftcard.domain.SaveResult;

public interface SaveGiftCardOrderGateway {

    SaveResult execute(GiftCardOrder giftCardOrder, IdempotentCommand idempotentCommand, String key);
}
