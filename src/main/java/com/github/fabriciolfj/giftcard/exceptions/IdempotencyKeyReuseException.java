package com.github.fabriciolfj.giftcard.exceptions;

public class IdempotencyKeyReuseException extends RuntimeException {

    public IdempotencyKeyReuseException(final String msg) {
        super(msg);
    }
}
