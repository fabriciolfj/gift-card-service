package com.github.fabriciolfj.giftcard.domain;

import com.github.f4b6a3.uuid.UuidCreator;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class GiftCardOrder {

    @Getter
    private final UUID uuid;
    private final Money money;
    private final Recipient recipient;
    private final ExpiryPolicy expiryPolicy;
    private final OrderStatus status;
    private final String purchaserRef;

    public static GiftCardOrder create(Money money, Recipient recipient, ExpiryPolicy expiryPolicy, String purchaserRef) {

        return new GiftCardOrder(UuidCreator.getTimeOrderedEpoch(),
                money,
                recipient,
                expiryPolicy,
                OrderStatus.PENDING_PAYMENT,
                purchaserRef);
    }

}
