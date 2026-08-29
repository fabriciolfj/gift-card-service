package com.github.fabriciolfj.giftcard.adapters.createordergiftrcard;

import com.github.fabriciolfj.giftcard.adapters.fingerprint.Fingerprint;
import com.github.fabriciolfj.giftcard.command.IdempotentCommand;
import com.github.fabriciolfj.giftcard.configurations.ExpiryProperties;
import com.github.fabriciolfj.giftcard.domain.GiftCardOrder;
import com.github.fabriciolfj.giftcard.domain.SaveResult;
import com.github.fabriciolfj.giftcard.exceptions.IdempotencyInProgressException;
import com.github.fabriciolfj.giftcard.exceptions.IdempotencyKeyReuseException;
import com.github.fabriciolfj.giftcard.persistences.repositories.GiftcardOrderRepository;
import com.github.fabriciolfj.giftcard.persistences.repositories.IdempotencyRepository;
import com.github.fabriciolfj.giftcard.usecases.createordergift.SaveGiftCardOrderGateway;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import static com.github.fabriciolfj.giftcard.util.ConstantsUtil.CORRELATION_ID;

@Component
public class SaveGiftCardOrderAdapter implements SaveGiftCardOrderGateway {

    private static final String ENDPOINT = "createGiftOrder";

    private final IdempotencyRepository idempotencyRepository;
    private final ExpiryProperties properties;
    private final GiftcardOrderRepository giftcardOrderRepository;

    public SaveGiftCardOrderAdapter(IdempotencyRepository idempotencyRepository, ExpiryProperties properties) {
        this.idempotencyRepository = idempotencyRepository;
        this.properties = properties;
    }

    @Override
    public SaveResult execute(final GiftCardOrder giftCardOrder, final IdempotentCommand idempotentCommand, String key) {

        final var fingerprint = Fingerprint.of(idempotentCommand);
        final var claimed = idempotencyRepository.tryClaim(fingerprint,
                key,
                MDC.get(CORRELATION_ID),
                ENDPOINT,
                properties.defaultPolicyRef());

        if (!claimed) {
            return replay(key, fingerprint);
        }

        giftcardOrderRepository.insert();
        return null;
    }

    private SaveResult replay(String key, String fingerprint) {
        final var existing = idempotencyRepository.load(key)
                .orElseThrow(IdempotencyInProgressException::new);

        if (!ENDPOINT.equals(existing.endpoint())) {
            throw new IdempotencyKeyReuseException(
                    "Chave pertence ao endpoint " + existing.endpoint());
        }

        if (!fingerprint.equals(existing.requestFingerprint())) {
            throw new IdempotencyKeyReuseException(
                    "Conteúdo difere da requisição original");
        }

        return new SaveResult.Replayed(existing.responseStatus(),
                existing.responseBody(),
                existing.responseLocation());
    }
}
