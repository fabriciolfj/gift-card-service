package com.github.fabriciolfj.giftcard.adapters.findparameter;

import com.github.fabriciolfj.giftcard.command.ParameterValidationCommand;
import com.github.fabriciolfj.giftcard.configurations.GiftCardOrderProperties;
import com.github.fabriciolfj.giftcard.usecases.createordergift.GetParametersValidateGiftCardGateway;
import org.springframework.stereotype.Component;

import static com.github.fabriciolfj.giftcard.adapters.findparameter.ParameterValidationCommandMapper.toCommand;

@Component
public class GetParametersValidateGiftCardAdapter implements GetParametersValidateGiftCardGateway {

    private final GiftCardOrderProperties properties;

    public GetParametersValidateGiftCardAdapter(GiftCardOrderProperties properties) {
        this.properties = properties;
    }

    @Override
    public ParameterValidationCommand process() {
        return toCommand(this.properties);
    }
}
