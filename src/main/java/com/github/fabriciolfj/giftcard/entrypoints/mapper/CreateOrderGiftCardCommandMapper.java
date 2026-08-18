package com.github.fabriciolfj.giftcard.entrypoints.mapper;

import com.github.fabriciolfj.giftcard.entrypoints.api.CreateGiftCardOrderRequest;
import com.github.fabriciolfj.giftcard.command.CreateOrderGiftCardOrderCommand;
import com.github.fabriciolfj.giftcard.command.RecipientCommand;

import java.util.Objects;

public class CreateOrderGiftCardCommandMapper {

    private CreateOrderGiftCardCommandMapper() { }

    public static CreateOrderGiftCardOrderCommand toCommand(final CreateGiftCardOrderRequest request) {
        final var command = CreateOrderGiftCardOrderCommand.builder()
                .amountCents(request.amountCents())
                .purcheaserRef(request.purchaserRef());

        if (Objects.nonNull(request.recipient())) {
            final var recipientCommand = RecipientCommand
                    .builder()
                    .email(request.getRecipientEmail())
                    .message(request.getRecipientMessage())
                    .name(request.getRecipientName())
                    .build();

            command.command(recipientCommand);
        }

        return command.build();
    }
}
