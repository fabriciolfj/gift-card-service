package com.github.fabriciolfj.giftcard.usecases.createordergift;

import com.github.fabriciolfj.giftcard.command.CreateOrderGiftCardOrderCommand;
import com.github.fabriciolfj.giftcard.command.ParameterValidationCommand;

public interface ValidateOrderGiftCardUsecase {

    void execute(CreateOrderGiftCardOrderCommand command, ParameterValidationCommand parameterValidationCommand);
}
