package com.github.fabriciolfj.giftcard.adapters.createordergiftrcard.replay;

import com.github.fabriciolfj.giftcard.exceptions.IdempotencyKeyReuseException;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * Mesma chave e mesmo endpoint, mas o conteúdo da requisição mudou. Nada é
 * executado: é para isso que o fingerprint existe.
 */
@Component
@Order(2)
public class FingerprintMatchesReplayRule implements IdempotencyReplayRule {

    @Override
    public void verify(final IdempotencyReplayContext context) {
        if (!context.expectedFingerprint().equals(context.stored().requestFingerprint())) {
            throw new IdempotencyKeyReuseException("Conteúdo difere da requisição original");
        }
    }
}
