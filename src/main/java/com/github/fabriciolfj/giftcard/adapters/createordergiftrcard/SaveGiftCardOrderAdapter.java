package com.github.fabriciolfj.giftcard.adapters.createordergiftrcard;

import com.github.fabriciolfj.giftcard.adapters.createordergiftrcard.replay.IdempotencyReplayContext;
import com.github.fabriciolfj.giftcard.adapters.createordergiftrcard.replay.IdempotencyReplayRule;
import com.github.fabriciolfj.giftcard.adapters.fingerprint.Fingerprint;
import com.github.fabriciolfj.giftcard.command.IdempotentCommand;
import com.github.fabriciolfj.giftcard.configurations.IdempotencyProperties;
import com.github.fabriciolfj.giftcard.domain.GiftCardOrder;
import com.github.fabriciolfj.giftcard.domain.SaveResult;
import com.github.fabriciolfj.giftcard.exceptions.IdempotencyInProgressException;
import com.github.fabriciolfj.giftcard.persistences.entities.GiftcardOrderEntityMapper;
import com.github.fabriciolfj.giftcard.persistences.repositories.GiftcardOrderRepository;
import com.github.fabriciolfj.giftcard.persistences.repositories.IdempotencyRepository;
import com.github.fabriciolfj.giftcard.usecases.createordergift.SaveGiftCardOrderGateway;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

import static com.github.fabriciolfj.giftcard.util.ConstantsUtil.CORRELATION_ID;

@Component
public class SaveGiftCardOrderAdapter implements SaveGiftCardOrderGateway {

    private static final String ENDPOINT = "createGiftOrder";

    private final IdempotencyRepository idempotencyRepository;
    private final GiftcardOrderRepository giftcardOrderRepository;
    private final IdempotencyProperties idempotencyProperties;
    private final List<IdempotencyReplayRule> replayRules;

    public SaveGiftCardOrderAdapter(final IdempotencyRepository idempotencyRepository,
                                    final GiftcardOrderRepository giftcardOrderRepository,
                                    final IdempotencyProperties idempotencyProperties,
                                    final List<IdempotencyReplayRule> replayRules) {
        this.idempotencyRepository = idempotencyRepository;
        this.giftcardOrderRepository = giftcardOrderRepository;
        this.idempotencyProperties = idempotencyProperties;
        this.replayRules = replayRules;
    }

    @Override
    public SaveResult execute(final GiftCardOrder giftCardOrder, final IdempotentCommand idempotentCommand, final String key) {

        final var fingerprint = Fingerprint.of(idempotentCommand);
        final var correlationId = MDC.get(CORRELATION_ID);

        final var claimed = idempotencyRepository.tryClaim(
                key, ENDPOINT, fingerprint, correlationId, idempotencyProperties.retention());

        if (!claimed) {
            return replay(key, fingerprint);
        }

        giftcardOrderRepository.insert(GiftcardOrderEntityMapper.toEntity(giftCardOrder, correlationId));
        return new SaveResult.Executed(giftCardOrder);
    }

    @Override
    public void complete(final String key, final int status, final String body, final String location, final UUID aggregateId) {
        idempotencyRepository.complete(key, status, body, location, aggregateId);
    }

    private SaveResult replay(final String key, final String fingerprint) {
        final var existing = idempotencyRepository.load(key)
                .orElseThrow(IdempotencyInProgressException::new);

        final var context = new IdempotencyReplayContext(ENDPOINT, fingerprint, existing);
        replayRules.forEach(rule -> rule.verify(context));

        return new SaveResult.Replayed(
                existing.responseStatus(),
                existing.responseBody(),
                existing.responseLocation());
    }
}
