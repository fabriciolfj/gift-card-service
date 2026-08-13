package com.github.fabriciolfj.giftcard.usecases.createordergift;

import com.github.fabriciolfj.giftcard.command.CreateOrderGiftCardCommand;
import com.github.fabriciolfj.giftcard.command.ParameterValidationCommand;
import com.github.fabriciolfj.giftcard.exceptions.AmountNotMultipleException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ValidateOrderGiftCardMultipleAmountUsecase implements ValidateOrderGiftCardUsecase{

    @Override
    public void execute(final CreateOrderGiftCardCommand command, final ParameterValidationCommand parameterValidationCommand) {
        final var result = command.amountCents() % parameterValidationCommand.multiple() == 0;

        if (result) {
            return;
        }

        log.error("value invalid, not multiple {} per {}", command.amountCents(), parameterValidationCommand.multiple());
        throw new AmountNotMultipleException(command.amountCents(), parameterValidationCommand.multiple());
    }
}
