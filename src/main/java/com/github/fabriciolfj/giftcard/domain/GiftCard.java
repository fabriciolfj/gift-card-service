package com.github.fabriciolfj.giftcard.domain;

import com.github.f4b6a3.uuid.UuidCreator;
import lombok.AllArgsConstructor;

import java.util.UUID;

@AllArgsConstructor
public class GiftCard {

    private UUID uuid;
    private Money money;
    private Recipient recipient;
    private ExpiryPolicy expiryPolicy;

    public static GiftCard create(Money money, Recipient recipient, ExpiryPolicy expiryPolicy) {

        return new GiftCard(UuidCreator.getTimeOrderedEpoch(), money, recipient, expiryPolicy);
    }
}
