package com.github.fabriciolfj.giftcard.exceptions;

public class InvalidGiftCardCodeException extends RuntimeException {

    public InvalidGiftCardCodeException(final String message) {
        super(message);
    }
}
