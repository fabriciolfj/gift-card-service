package com.github.fabriciolfj.giftcard.entrypoints.api;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.Instant;
import java.util.UUID;


@JsonInclude(JsonInclude.Include.NON_NULL)
public record GiftCardOrderResponse(

        UUID id,
        String status,
        MoneyResponse amount,
        String purchaserRef,

        String expiryPolicyRef,

        UUID giftCardId,

        Instant createdAt,
        Instant activatedAt
) {
}