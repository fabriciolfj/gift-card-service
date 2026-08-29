package com.github.fabriciolfj.giftcard.persistences.entities;

import com.github.fabriciolfj.giftcard.domain.GiftCardOrder;
import com.github.fabriciolfj.giftcard.domain.Recipient;

/**
 * Fica neste pacote para alcançar os construtores package-private de
 * {@link GiftcardOrderEntity} e {@link RecipientEmbeddable}.
 */
public final class GiftcardOrderEntityMapper {

    private GiftcardOrderEntityMapper() { }

    public static GiftcardOrderEntity toEntity(final GiftCardOrder order, final String correlationId) {
        if (order.getExpiryPolicy() == null) {
            throw new IllegalStateException("expiry policy deve ser resolvida antes da persistência (RN-16)");
        }

        return new GiftcardOrderEntity(
                order.getUuid(),
                order.getMoney().cents(),
                order.getMoney().currency().name(),
                order.getStatus().name(),
                order.getExpiryPolicy().ref(),
                order.getPurchaserRef(),
                toRecipient(order.getRecipient()),
                correlationId);
    }

    private static RecipientEmbeddable toRecipient(final Recipient recipient) {
        if (recipient == null) {
            return null;
        }
        return new RecipientEmbeddable(recipient.name(), recipient.email(), recipient.message());
    }
}
