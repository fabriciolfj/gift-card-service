package com.github.fabriciolfj.giftcard.entrypoints.api;

public record ActivateGiftCardOrderResponse(String code) {

    @Override
    public String toString() {
        return "ActivateGiftCardOrderResponse[code=****]";
    }
}
