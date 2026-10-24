package com.github.fabriciolfj.giftcard.exceptions;

import lombok.Getter;

@Getter
public class PaymentAmountMismatchException extends RuntimeException {

    private final Long expectedCents;
    private final Long paidCents;

    public PaymentAmountMismatchException(Long expectedCents, Long paidCents) {
        this.expectedCents = expectedCents;
        this.paidCents = paidCents;
    }
}
