package com.github.fabriciolfj.giftcard.exceptions;

import lombok.Getter;

import java.util.UUID;

@Getter
public class GiftCardOrderNotFoundException extends RuntimeException {

    private final UUID orderId;

    public GiftCardOrderNotFoundException(final UUID orderId) {
        super("pedido %s não encontrado".formatted(orderId));
        this.orderId = orderId;
    }
}
