package com.github.fabriciolfj.giftcard.usecases.common;

import com.github.fabriciolfj.giftcard.command.CreateOrderGiftCardOrderCommand;
import com.github.fabriciolfj.giftcard.domain.GiftCardOrder;
import com.github.fabriciolfj.giftcard.domain.Money;
import com.github.fabriciolfj.giftcard.domain.Recipient;

public class GiftCardOrderMapper {

    private GiftCardOrderMapper() { }

    public static GiftCardOrder of(CreateOrderGiftCardOrderCommand command) {
        final var money = Money.ofCents(command.amountCents());
        final var recipientCommand = command.recipientCommand();

        Recipient recipient = null;
        if (recipientCommand != null) {
            recipient = new Recipient(recipientCommand.name(), recipientCommand.email(), recipientCommand.message());
        }

        return GiftCardOrder.create(money, recipient, null, command.purcheaserRef());
    }
}
