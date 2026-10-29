package com.github.fabriciolfj.giftcard.persistences.entities;

import com.github.fabriciolfj.giftcard.domain.Currency;
import com.github.fabriciolfj.giftcard.domain.ExpiryPolicy;
import com.github.fabriciolfj.giftcard.domain.GiftCardOrder;
import com.github.fabriciolfj.giftcard.domain.Money;
import com.github.fabriciolfj.giftcard.domain.OrderStatus;
import com.github.fabriciolfj.giftcard.domain.Recipient;


public final class GiftCardOrderEntityMapper {

    private GiftCardOrderEntityMapper() { }

    public static GiftCardOrder toDomain(final GiftCardOrderEntity entity) {
        return GiftCardOrder.rehydrate(
                entity.getId(),
                new Money(entity.getAmountCents(), Currency.valueOf(entity.getCurrency())),
                toRecipient(entity.getRecipient()),
                new ExpiryPolicy(entity.getExpiryPolicyRef(), null),
                OrderStatus.valueOf(entity.getStatus()),
                entity.getPurchaserRef());
    }

    public static GiftCardOrderEntity toEntity(final GiftCardOrder order, final String correlationId) {
        if (order.getExpiryPolicy() == null) {
            throw new IllegalStateException("expiry policy deve ser resolvida antes da persistência (RN-16)");
        }

        return new GiftCardOrderEntity(
                order.getUuid(),
                order.getMoney().cents(),
                order.getMoney().currency().name(),
                order.getStatus().name(),
                order.getExpiryPolicy().ref(),
                order.getPurchaserRef(),
                toRecipient(order.getRecipient()),
                correlationId);
    }

    private static Recipient toRecipient(final RecipientEmbeddable recipient) {
        if (recipient == null || recipient.isEmpty()) {
            return null;
        }
        return new Recipient(recipient.getName(), recipient.getEmail(), recipient.getMessage());
    }

    private static RecipientEmbeddable toRecipient(final Recipient recipient) {
        if (recipient == null) {
            return null;
        }
        return new RecipientEmbeddable(recipient.name(), recipient.email(), recipient.message());
    }
}
