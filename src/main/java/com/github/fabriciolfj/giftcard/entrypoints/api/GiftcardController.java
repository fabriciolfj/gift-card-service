package com.github.fabriciolfj.giftcard.entrypoints.api;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static com.github.fabriciolfj.giftcard.entrypoints.mapper.CreateOrderGiftCardCommandMapper.toCommand;
import static com.github.fabriciolfj.giftcard.util.ConstantsUtil.CORRELATION_ID;
import static com.github.fabriciolfj.giftcard.util.ConstantsUtil.IDEMPOTENCY_KEY;

@Slf4j
@Validated
@RestController
@RequestMapping("/api/v1/giftcard")
public class GiftcardController {


    @PostMapping
    public GiftCardOrderResponse create(@Valid @RequestBody CreateGiftCardOrderRequest request,
                                        @RequestHeader(value = "Idempotency-Key", required = true) String key) {

        log.info("receive request to create order gift {}", request);
        MDC.put(IDEMPOTENCY_KEY, key);

        final var command = toCommand(request);
        MDC.clear();
        return null;
    }

}
