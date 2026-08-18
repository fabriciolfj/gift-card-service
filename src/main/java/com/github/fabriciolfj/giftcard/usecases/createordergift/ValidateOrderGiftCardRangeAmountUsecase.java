package com.github.fabriciolfj.giftcard.usecases.createordergift;

import com.github.fabriciolfj.giftcard.command.CreateOrderGiftCardOrderCommand;
import com.github.fabriciolfj.giftcard.command.ParameterValidationCommand;
import com.github.fabriciolfj.giftcard.exceptions.AmountOutOfRangeException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import static com.github.fabriciolfj.giftcard.util.BetweenUtil.isBetween;

@Slf4j
@Service
@Order(2)
public class ValidateOrderGiftCardRangeAmountUsecase implements ValidateOrderGiftCardUsecase {

    @Override
    public void execute(CreateOrderGiftCardOrderCommand command, ParameterValidationCommand parameterValidationCommand) {
        if (isBetween(command.amountCents(), parameterValidationCommand.minAmount(), parameterValidationCommand.maxAmount())) {
            return;
        }

        log.info("amount request {} not between {} {}", command.amountCents(), parameterValidationCommand.minAmount(), parameterValidationCommand.maxAmount());

        throw new AmountOutOfRangeException(
                parameterValidationCommand.minAmount(),
                parameterValidationCommand.maxAmount(),
                command.amountCents());

    }
}
