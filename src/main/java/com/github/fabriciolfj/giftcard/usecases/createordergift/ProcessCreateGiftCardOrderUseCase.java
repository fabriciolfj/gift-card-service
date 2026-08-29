package com.github.fabriciolfj.giftcard.usecases.createordergift;

import com.github.fabriciolfj.giftcard.command.CreateOrderGiftCardOrderCommand;
import com.github.fabriciolfj.giftcard.usecases.createordergift.CreateOrderOutputBoundary.RenderedResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class ProcessCreateGiftCardOrderUseCase {

    private final OrchestratorCreateOrderGiftCardUsecase orchestratorCreateOrderGiftCardUsecase;
    private final SaveGiftCardOrderGateway saveGateway;
    private final CreateOrderOutputBoundary outputBoundary;

    public ProcessCreateGiftCardOrderUseCase(final OrchestratorCreateOrderGiftCardUsecase orchestratorCreateOrderGiftCardUsecase,
                                             final SaveGiftCardOrderGateway saveGateway, CreateOrderOutputBoundary outputBoundary) {
        this.orchestratorCreateOrderGiftCardUsecase = orchestratorCreateOrderGiftCardUsecase;
        this.saveGateway = saveGateway;
        this.outputBoundary = outputBoundary;
    }

    @Transactional
    public RenderedResponse execute(final CreateOrderGiftCardOrderCommand command, final String key) {
        final var giftOrder = orchestratorCreateOrderGiftCardUsecase.execute(command);
        log.info("gift order validated {}", giftOrder.getUuid());

        final var result = saveGateway.execute(giftOrder, command, key);

        return switch (result){
            case SaveResult.Executed(var saved) -> {
                final var rendered = outputBoundary.render(saved);
                saveGateway.complete(key, rendered.status(), rendered.body(), rendered.location(), saved.getUuid());
                yield rendered;
            }
            case SaveResult.Replayed(int status, String body, String location) ->
                    new RenderedResponse(status, body, location);
        };
    }

}
