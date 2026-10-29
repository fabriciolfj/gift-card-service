package com.github.fabriciolfj.giftcard.entrypoints.api;

import com.github.fabriciolfj.giftcard.domain.Money;
import com.github.fabriciolfj.giftcard.usecases.creategiftcard.CreateGiftCardUsecase;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Slf4j
@Validated
@RestController
@RequestMapping("/v1/gift-card-orders")
public class GiftCardActivationController {

    private final CreateGiftCardUsecase createGiftCardUsecase;

    public GiftCardActivationController(final CreateGiftCardUsecase createGiftCardUsecase) {
        this.createGiftCardUsecase = createGiftCardUsecase;
    }

    @PostMapping("/{orderId}/activate")
    public ResponseEntity<ActivateGiftCardOrderResponse> activate(@PathVariable final UUID orderId,
                                         @Valid @RequestBody final ActivateGiftCardOrderRequest request) {

        log.info("receive request to activate order gift {}", orderId);
        final var code = createGiftCardUsecase.execute(orderId,
                request.paymentRef(),
                Money.ofCents(request.paidAmountCents()),
                request.paidAt());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ActivateGiftCardOrderResponse(code));
    }
}
