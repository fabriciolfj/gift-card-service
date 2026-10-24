package com.github.fabriciolfj.giftcard.persistences.entities;

import com.github.fabriciolfj.giftcard.domain.Currency;
import com.github.fabriciolfj.giftcard.domain.GiftCard;

public final class GiftcardEntityMapper {

    private GiftcardEntityMapper() {

    }

    public static GiftcardEntity toEntity(final GiftCard giftCard) {
        return new GiftcardEntity(
                giftCard.getUuid(),
                giftCard.getCodeHash(),
                giftCard.getCodeLast4(),
                giftCard.getStatus(),
                giftCard.getCurrency().name(),
                giftCard.getExpiresAt(),
                null,
                giftCard.getVersion() == null ? 0L : giftCard.getVersion(),
                null,
                null
        );
    }

    public static GiftCard toDomain(final GiftcardEntity entity) {
        return GiftCard.rehydrate(
                entity.getId(),
                entity.getCodeHash(),
                entity.getCodeLast4(),
                entity.getStatus(),
                Currency.valueOf(entity.getCurrency()),
                entity.getExpiresAt(),
                entity.getVersion()
        );
    }
}
