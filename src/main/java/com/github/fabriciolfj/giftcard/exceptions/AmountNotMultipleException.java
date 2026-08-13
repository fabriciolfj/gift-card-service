package com.github.fabriciolfj.giftcard.exceptions;

import lombok.Getter;

@Getter
public class AmountNotMultipleException extends RuntimeException {

    private Long multipleCents;
    private Long requestedCents;

    public AmountNotMultipleException(final Long multipleCents, final Long requestedCents) {
        this.multipleCents = multipleCents;
        this.requestedCents = requestedCents;
    }
}
