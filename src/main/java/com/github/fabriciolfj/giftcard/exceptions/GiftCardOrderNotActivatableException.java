package com.github.fabriciolfj.giftcard.exceptions;

import lombok.Getter;

import java.util.UUID;

@Getter
public class GiftCardOrderNotActivatableException extends RuntimeException {

    private final UUID orderId;

    public GiftCardOrderNotActivatableException(final UUID orderId) {
        super("pedido %s não encontrado ou não está em PENDING_PAYMENT".formatted(orderId));
        this.orderId = orderId;
    }
}
