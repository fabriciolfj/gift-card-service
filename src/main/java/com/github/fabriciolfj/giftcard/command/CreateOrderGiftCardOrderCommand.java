package com.github.fabriciolfj.giftcard.command;

import lombok.Builder;

@Builder
public record CreateOrderGiftCardOrderCommand(Long amountCents, String purcheaserRef, RecipientCommand recipientCommand) { }
