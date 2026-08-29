package com.github.fabriciolfj.giftcard.usecases.createordergift;

import com.github.fabriciolfj.giftcard.command.IdempotentCommand;
import com.github.fabriciolfj.giftcard.domain.GiftCardOrder;

import java.util.UUID;

public interface SaveGiftCardOrderGateway {

    SaveResult execute(GiftCardOrder giftCardOrder, IdempotentCommand idempotentCommand, String key);

    void complete(String key, int status, String body, String location, UUID aggregateId);
}
