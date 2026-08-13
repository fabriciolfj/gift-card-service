package com.github.fabriciolfj.giftcard.usecases.common;

import com.github.fabriciolfj.giftcard.command.CreateOrderGiftCardCommand;
import com.github.fabriciolfj.giftcard.domain.GiftCard;
import com.github.fabriciolfj.giftcard.domain.Money;
import com.github.fabriciolfj.giftcard.domain.Recipient;

public class GiftCardMapper {

    private GiftCardMapper() { }

    public static GiftCard of(CreateOrderGiftCardCommand command) {
        var money = Money.ofCents(command.amountCents());
        var recipient = new Recipient(command.getName(), command.getEmail(), command.getMessage());

        return GiftCard.create(money, recipient, null);
    }
}
