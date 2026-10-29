package com.github.fabriciolfj.giftcard.entrypoints.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.time.Instant;

@JsonIgnoreProperties(ignoreUnknown = false)
public record ActivateGiftCardOrderRequest(

        @NotBlank(message = "paymentRef é obrigatório")
        @Size(max = 64, message = "paymentRef deve ter no máximo 64 caracteres")
        String paymentRef,

        @NotNull(message = "paidAmountCents é obrigatório")
        @Positive(message = "paidAmountCents deve ser positivo")
        Long paidAmountCents,

        @NotNull(message = "paidAt é obrigatório")
        @PastOrPresent(message = "paidAt não pode estar no futuro")
        Instant paidAt
) {
}
