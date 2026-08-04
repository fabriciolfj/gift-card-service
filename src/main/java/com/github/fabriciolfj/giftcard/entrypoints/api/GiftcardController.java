package com.github.fabriciolfj.giftcard.entrypoints.api;

import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequestMapping("/api/v1/giftcard")
public class GiftcardController {


    @PostMapping
    public GiftCardOrderResponse create(@Valid @RequestBody CreateGiftCardOrderRequest request,
                                        @RequestHeader(value = "Idempotency-Key", required = true) String key,
                                        @RequestHeader(value = "X-Correlation-Id", required = true) String correlationId) {
        return null;
    }

}
