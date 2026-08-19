package com.github.fabriciolfj.giftcard.usecases.createordergift;

import com.github.fabriciolfj.giftcard.command.CreateOrderGiftCardOrderCommand;
import com.github.fabriciolfj.giftcard.domain.GiftCardOrder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ProcessCreateGiftCardOrderUseCase {

    private final OrchestratorCreateOrderGiftCardUsecase orchestratorCreateOrderGiftCardUsecase;
    private final SaveGiftCardOrderGateway saveGiftCardOrderGateway;

    public ProcessCreateGiftCardOrderUseCase(OrchestratorCreateOrderGiftCardUsecase orchestratorCreateOrderGiftCardUsecase, SaveGiftCardOrderGateway saveGiftCardOrderGateway) {
        this.orchestratorCreateOrderGiftCardUsecase = orchestratorCreateOrderGiftCardUsecase;
        this.saveGiftCardOrderGateway = saveGiftCardOrderGateway;
    }

    public GiftCardOrder execute(final CreateOrderGiftCardOrderCommand command, final String key) {
        final var giftOrder = orchestratorCreateOrderGiftCardUsecase.execute(command);

        log.info("gift order validated {}", giftOrder.getUuid());
        return saveGiftCardOrderGateway.execute(giftOrder, command, key);
    }
}
