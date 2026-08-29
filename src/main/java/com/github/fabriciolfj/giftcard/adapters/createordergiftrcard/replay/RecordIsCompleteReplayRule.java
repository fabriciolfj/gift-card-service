package com.github.fabriciolfj.giftcard.adapters.createordergiftrcard.replay;

import com.github.fabriciolfj.giftcard.exceptions.IdempotencyInProgressException;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * Guarda para o desenho de transação única: se a linha está visível ela deveria
 * estar completa. Se não está, há transação concorrente ainda sem commit.
 */
@Component
@Order(3)
public class RecordIsCompleteReplayRule implements IdempotencyReplayRule {

    @Override
    public void verify(final IdempotencyReplayContext context) {
        if (!context.stored().isComplete()) {
            throw new IdempotencyInProgressException();
        }
    }
}
