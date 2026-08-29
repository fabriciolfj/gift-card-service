package com.github.fabriciolfj.giftcard.adapters.createordergiftrcard.replay;

import com.github.fabriciolfj.giftcard.exceptions.IdempotencyKeyReuseException;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;


@Component
@Order(1)
public class EndpointMatchesReplayRule implements IdempotencyReplayRule {

    @Override
    public void verify(final IdempotencyReplayContext context) {
        final var stored = context.stored();

        if (!context.expectedEndpoint().equals(stored.endpoint())) {
            throw new IdempotencyKeyReuseException("Chave pertence ao endpoint " + stored.endpoint());
        }
    }
}
