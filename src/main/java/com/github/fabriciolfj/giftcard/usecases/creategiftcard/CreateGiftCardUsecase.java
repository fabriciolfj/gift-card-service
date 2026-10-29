package com.github.fabriciolfj.giftcard.usecases.creategiftcard;

import com.github.fabriciolfj.giftcard.domain.GiftCard;
import com.github.fabriciolfj.giftcard.domain.Money;
import com.github.fabriciolfj.giftcard.exceptions.GiftCardOrderNotFoundException;
import com.github.fabriciolfj.giftcard.usecases.common.GetGiftCardOrderGateway;
import com.github.fabriciolfj.giftcard.usecases.creategiftcardcode.GiftCardCodeGenerator;
import com.github.fabriciolfj.giftcard.usecases.creategiftcardcode.GiftCardCodeHasher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.ZoneId;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CreateGiftCardUsecase {

    private static final ZoneId ZONE = ZoneId.of("America/Sao_Paulo");

    private final GetGiftCardOrderGateway getGiftCardOrderGateway;
    private final SaveGiftCardGateway saveGiftCardGateway;
    private final GiftCardCodeGenerator giftCardCodeGenerator;
    private final GiftCardCodeHasher codeHasher;
    private final ActivateGiftorderGateway activateGiftorderGateway;

    @Transactional
    public String execute(final UUID uuid,
                        final String paymentRef,
                        final Money paidAmount,
                        final Instant paidAt) {

        final var giftCardOrder = getGiftCardOrderGateway.findByOrder(uuid)
                .orElseThrow(() -> new GiftCardOrderNotFoundException(uuid));

        final var activatedAt = Instant.now();
        final var code = giftCardCodeGenerator.generate();

        final var expiresAt = activatedAt.atZone(ZONE)
                .plus(giftCardOrder.getDuration())
                .toInstant();

        final var giftCard = GiftCard.create(
                codeHasher.hash(code),
                code.last4(),
                expiresAt);

        final var orderActivate = giftCardOrder.activate(giftCard.getUuid(), paymentRef, paidAmount, paidAt, activatedAt);

        saveGiftCardGateway.execute(giftCard);
        activateGiftorderGateway.process(orderActivate);

        return code.formatted();
    }
}
