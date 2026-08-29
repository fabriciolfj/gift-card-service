package com.github.fabriciolfj.giftcard.usecases.createordergift;

import com.github.fabriciolfj.giftcard.domain.GiftCardOrder;

public interface CreateOrderOutputBoundary {

    RenderedResponse render(GiftCardOrder order);

    record RenderedResponse(int status, String body, String location) { }
}
