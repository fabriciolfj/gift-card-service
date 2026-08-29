package com.github.fabriciolfj.giftcard.entrypoints.presenter;

import com.github.fabriciolfj.giftcard.domain.GiftCardOrder;
import com.github.fabriciolfj.giftcard.entrypoints.mapper.GiftCardOrderResponseMapper;
import com.github.fabriciolfj.giftcard.usecases.createordergift.CreateOrderOutputBoundary;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import tools.jackson.databind.json.JsonMapper;

@Component
@RequiredArgsConstructor
public class CreateOrderPresenter implements CreateOrderOutputBoundary {

    private static final String LOCATION_PREFIX = "/v1/gift-card-orders/";

    private final JsonMapper jsonMapper;

    @Override
    public RenderedResponse render(final GiftCardOrder order) {
        return new RenderedResponse(
                HttpStatus.CREATED.value(),
                jsonMapper.writeValueAsString(
                        GiftCardOrderResponseMapper.toResponse(order)),
                LOCATION_PREFIX + order.getUuid());
    }
}