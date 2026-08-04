package com.github.fabriciolfj.giftcard.exceptions;

import lombok.Getter;

@Getter
public class AmountOutOfRangeException extends RuntimeException {

    private Long minCents;
    private Long maxCents;
    private Long requestCents;

    public AmountOutOfRangeException(Long minCents, Long maxCents, Long requestCents) {
        this.maxCents = maxCents;
        this.minCents = minCents;
        this.requestCents =requestCents;
    }
}
