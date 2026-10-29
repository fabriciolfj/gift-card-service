package com.github.fabriciolfj.giftcard.usecases.common;

import com.github.fabriciolfj.giftcard.domain.GiftCardOrder;

import java.util.Optional;
import java.util.UUID;

public interface GetGiftCardOrderGateway {

    Optional<GiftCardOrder> findByOrder(final UUID uuid);
}
