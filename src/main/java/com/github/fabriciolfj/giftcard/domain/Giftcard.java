package com.github.fabriciolfj.giftcard.domain;

import com.github.f4b6a3.uuid.UuidCreator;
import lombok.AllArgsConstructor;

import java.util.UUID;

@AllArgsConstructor
public class Giftcard {

    private UUID uuid;
    private Money money;

    public static Giftcard create(Money money) {

        return new Giftcard(UuidCreator.getTimeOrderedEpoch(), money);
    }
}
