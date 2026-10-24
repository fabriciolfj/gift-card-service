package com.github.fabriciolfj.giftcard.exceptions;

import com.github.fabriciolfj.giftcard.domain.OrderStatus;
import lombok.Getter;

@Getter
public class InvalidStateTransitionException extends RuntimeException {

    private final OrderStatus currentStatus;
    private final OrderStatus targetStatus;

    public InvalidStateTransitionException(final OrderStatus currentStatus,
                                           final OrderStatus targetStatus) {
        this.currentStatus = currentStatus;
        this.targetStatus = targetStatus;
    }
}