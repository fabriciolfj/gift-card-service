package com.github.fabriciolfj.giftcard.command;

import lombok.Builder;

@Builder
public record CreateOrderGiftCardOrderCommand(Long amountCents, String purcheaserRef, RecipientCommand command) {

    public String getName() {
        return this.command.name();
    }

    public String getEmail() {
        return this.command.email();
    }

    public String getMessage() {
        return this.command.message();
    }
}
