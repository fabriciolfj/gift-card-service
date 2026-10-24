package com.github.fabriciolfj.giftcard.domain;

import com.github.f4b6a3.uuid.UuidCreator;
import com.github.fabriciolfj.giftcard.exceptions.InvalidStateTransitionException;
import com.github.fabriciolfj.giftcard.exceptions.PaymentAmountMismatchException;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class GiftCardOrder {

    private final UUID uuid;
    private final Money money;
    private final Recipient recipient;
    private final ExpiryPolicy expiryPolicy;
    private final OrderStatus status;
    private final String purchaserRef;
    private final UUID giftCardId;
    private final String paymentRef;
    private final Money paidAmount;
    private final Instant paidAt;
    private final Instant activatedAt;

    public static GiftCardOrder create(Money money, Recipient recipient, ExpiryPolicy expiryPolicy, String purchaserRef) {

        return new GiftCardOrder(UuidCreator.getTimeOrderedEpoch(),
                money,
                recipient,
                expiryPolicy,
                OrderStatus.PENDING_PAYMENT,
                purchaserRef,
                null,
                null,
                null,
                null,
                null);
    }

    public static GiftCardOrder rehydrate(UUID uuid,
                                          Money money,
                                          Recipient recipient,
                                          ExpiryPolicy policy,
                                          OrderStatus status,
                                          String purchaserRef) {

        return new GiftCardOrder(uuid,
                money,
                recipient,
                policy,
                status,
                purchaserRef,
                null,
                null,
                null,
                null,
                null);
    }

    public GiftCardOrder activate(UUID giftCardId,
                                  String paymentRef,
                                  Money paidAmount,
                                  Instant paidAt) {

        if (status != OrderStatus.PENDING_PAYMENT) {
            throw new InvalidStateTransitionException(status, null);
        }

        if (!money.equals(paidAmount)) {
            throw new PaymentAmountMismatchException(money.cents(), paidAmount.cents());
        }

        return new GiftCardOrder(uuid,
                money,
                recipient,
                expiryPolicy,
                status,
                purchaserRef,
                giftCardId,
                paymentRef,
                paidAmount,
                paidAt,
                Instant.now());
    }

}
