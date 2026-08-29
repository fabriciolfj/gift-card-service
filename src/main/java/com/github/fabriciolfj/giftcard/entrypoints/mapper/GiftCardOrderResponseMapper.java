package com.github.fabriciolfj.giftcard.entrypoints.mapper;

import com.github.fabriciolfj.giftcard.domain.GiftCardOrder;
import com.github.fabriciolfj.giftcard.entrypoints.api.GiftCardOrderResponse;
import com.github.fabriciolfj.giftcard.entrypoints.api.MoneyResponse;

public class GiftCardOrderResponseMapper {

    private GiftCardOrderResponseMapper() { }

    public static GiftCardOrderResponse toResponse(final GiftCardOrder order) {
        return new GiftCardOrderResponse(
                order.getUuid(),
                order.getStatus().name(),
                new MoneyResponse(order.getMoney().cents(), order.getMoney().currency().name()),
                order.getPurchaserRef(),
                order.getExpiryPolicy() != null ? order.getExpiryPolicy().ref() : null,
                null,   // giftCardId: só nasce na ativação (T-014)
                null,   // createdAt: não trafegado pelo domínio nesta etapa
                null);  // activatedAt: idem
    }
}
