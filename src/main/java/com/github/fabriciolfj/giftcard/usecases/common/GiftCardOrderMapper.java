package com.github.fabriciolfj.giftcard.usecases.common;

import com.github.fabriciolfj.giftcard.command.CreateOrderGiftCardOrderCommand;
import com.github.fabriciolfj.giftcard.domain.GiftCardOrder;
import com.github.fabriciolfj.giftcard.domain.Money;
import com.github.fabriciolfj.giftcard.domain.Recipient;

public class GiftCardOrderMapper {

    private GiftCardOrderMapper() { }

    public static GiftCardOrder of(CreateOrderGiftCardOrderCommand command) {
        var money = Money.ofCents(command.amountCents());
        var recipient = new Recipient(command.getName(), command.getEmail(), command.getMessage());

        return GiftCardOrder.create(money, recipient, null, command.purcheaserRef());
    }
}
