package com.github.fabriciolfj.giftcard.entrypoints.api;

import com.github.fabriciolfj.giftcard.domain.SaveResult;
import com.github.fabriciolfj.giftcard.entrypoints.mapper.GiftCardOrderResponseMapper;
import com.github.fabriciolfj.giftcard.usecases.createordergift.ProcessCreateGiftCardOrderUseCase;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

import static com.github.fabriciolfj.giftcard.entrypoints.mapper.CreateOrderGiftCardCommandMapper.toCommand;

@Slf4j
@Validated
@RestController
@RequestMapping("/api/v1/giftcard")
public class GiftcardController {

    private final ProcessCreateGiftCardOrderUseCase processCreateGiftCardOrderUseCase;

    public GiftcardController(final ProcessCreateGiftCardOrderUseCase processCreateGiftCardOrderUseCase) {
        this.processCreateGiftCardOrderUseCase = processCreateGiftCardOrderUseCase;
    }

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody final CreateGiftCardOrderRequest request,
                                    @RequestHeader("Idempotency-Key")
                                    @Size(min = 16, max = 64, message = "Idempotency-Key deve ter entre 16 e 64 caracteres")
                                    final String idempotencyKey) {

        log.info("receive request to create order gift {}", request);
        final var response = processCreateGiftCardOrderUseCase.execute(toCommand(request), idempotencyKey);

        return ResponseEntity.status(response.status())
                .header(HttpHeaders.LOCATION, response.location())
                .body(response.body());
    }
}
