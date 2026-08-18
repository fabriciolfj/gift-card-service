package com.github.fabriciolfj.giftcard.usecases.createordergift;

import com.github.fabriciolfj.giftcard.domain.GiftCardOrder;
import com.github.fabriciolfj.giftcard.command.CreateOrderGiftCardOrderCommand;
import com.github.fabriciolfj.giftcard.usecases.common.GiftCardOrderMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class OrchestratorCreateOrderGiftCardUsecase {

    private final GetParametersValidateGiftCardGateway getParametersGateway;
    private final List<ValidateOrderGiftCardUsecase> validateOrderGiftCardUsecases;

    public OrchestratorCreateOrderGiftCardUsecase(final GetParametersValidateGiftCardGateway getParametersGateway,
                                                  final List<ValidateOrderGiftCardUsecase> validateOrderGiftCardUsecases) {
        this.getParametersGateway = getParametersGateway;
        this.validateOrderGiftCardUsecases = validateOrderGiftCardUsecases;
    }

    public GiftCardOrder execute(final CreateOrderGiftCardOrderCommand command) {
        log.info("init validation create order card");

        final var parameter = getParametersGateway.process();
        validateOrderGiftCardUsecases.forEach(v -> v.execute(command, parameter));

        return GiftCardOrderMapper.of(command);
    }
}
