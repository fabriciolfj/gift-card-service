package com.github.fabriciolfj.giftcard.domain;

import com.github.f4b6a3.uuid.UuidCreator;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class GiftCard {

    private UUID uuid;
    private String codeHash;
    private String codeLast4;
    private GiftCardStatus status;
    private Currency currency;
    private Instant expiresAt;
    private Long version;

    public static GiftCard create(String codeHash, String codeLast4, Instant expiresAt) {
        return new GiftCard(
                UuidCreator.getTimeOrderedEpoch(),
                codeHash,
                codeLast4,
                GiftCardStatus.ACTIVE,
                Currency.BRL,
                expiresAt,
                0L
        );
    }

    public static GiftCard rehydrate(UUID uuid,
                                     String codeHash,
                                     String codeLast4,
                                     GiftCardStatus status,
                                     Currency currency,
                                     Instant expiresAt,
                                     Long version) {
        return new GiftCard(
                uuid,
                codeHash,
                codeLast4,
                status,
                currency,
                expiresAt,
                version
        );
    }

}
