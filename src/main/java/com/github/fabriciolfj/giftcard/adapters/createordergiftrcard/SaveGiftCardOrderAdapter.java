package com.github.fabriciolfj.giftcard.adapters.createordergiftrcard;

import com.github.fabriciolfj.giftcard.command.IdempotentCommand;
import com.github.fabriciolfj.giftcard.domain.Fingerprint;
import com.github.fabriciolfj.giftcard.domain.GiftCardOrder;
import com.github.fabriciolfj.giftcard.persistences.repositories.IdempotencyRepository;
import com.github.fabriciolfj.giftcard.usecases.createordergift.SaveGiftCardOrderGateway;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import static com.github.fabriciolfj.giftcard.util.ConstantsUtil.*;

@Component
public class SaveGiftCardOrderAdapter implements SaveGiftCardOrderGateway {

    private final IdempotencyRepository idempotencyRepository;

    public SaveGiftCardOrderAdapter(IdempotencyRepository idempotencyRepository) {
        this.idempotencyRepository = idempotencyRepository;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public GiftCardOrder execute(final GiftCardOrder giftCardOrder, final IdempotentCommand idempotentCommand) {

        final var fingerprint = Fingerprint.of(idempotentCommand);
        final var claimed = idempotencyRepository.tryClaim(fingerprint,
                MDC.get(IDEMPOTENCY_KEY),
                MDC.get(CORRELATION_ID),
                MDC.get(ENDPOINT));
        return null;
    }
}
