package com.github.fabriciolfj.giftcard.entrypoints.api;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

@JsonIgnoreProperties(ignoreUnknown = true)
public record CreateGiftCardOrderRequest(

        @NotNull(message = "amountCents é obrigatório")
        @Positive(message = "amountCents deve ser positivo")
        Long amountCents,

        @Size(max = 64, message = "purchaserRef deve ter no máximo 64 caracteres")
        String purchaserRef,
        @Valid
        RecipientRequest recipient
) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record RecipientRequest(

            @Size(max = 120, message = "recipient.name deve ter no máximo 120 caracteres")
            String name,

            @NotBlank(message = "recipient.email é obrigatório quando recipient é informado")
            @Email(message = "recipient.email deve ser um endereço válido")
            @Size(max = 255, message = "recipient.email deve ter no máximo 255 caracteres")
            String email,

            @Size(max = 280, message = "recipient.message deve ter no máximo 280 caracteres")
            String message
    ) {
    }

    public String getRecipientName() {
        return this.recipient.name;
    }

    public String getRecipientEmail() {
        return this.recipient.email;
    }

    public String getRecipientMessage() {
        return this.recipient.message;
    }
}