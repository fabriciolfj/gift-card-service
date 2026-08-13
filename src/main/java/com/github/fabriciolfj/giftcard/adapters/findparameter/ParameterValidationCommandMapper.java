package com.github.fabriciolfj.giftcard.adapters.findparameter;

import com.github.fabriciolfj.giftcard.command.ParameterValidationCommand;
import com.github.fabriciolfj.giftcard.configurations.GiftCardOrderProperties;

public class ParameterValidationCommandMapper {

    private ParameterValidationCommandMapper() { }

    public static ParameterValidationCommand toCommand(final GiftCardOrderProperties properties) {
        return ParameterValidationCommand.builder()
                .maxAmount(properties.maxAmountCents())
                .minAmount(properties.minAmountCents())
                .multiple(properties.amountMultipleCents())
                .build();
    }
}
