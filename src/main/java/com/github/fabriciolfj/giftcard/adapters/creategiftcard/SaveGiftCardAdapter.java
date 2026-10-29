package com.github.fabriciolfj.giftcard.adapters.creategiftcard;

import com.github.fabriciolfj.giftcard.domain.GiftCard;
import com.github.fabriciolfj.giftcard.persistences.entities.GiftCardEntityMapper;
import com.github.fabriciolfj.giftcard.persistences.repositories.GiftCardRepository;
import com.github.fabriciolfj.giftcard.usecases.creategiftcard.SaveGiftCardGateway;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SaveGiftCardAdapter implements SaveGiftCardGateway {

    private final GiftCardRepository repository;

    @Override
    public void execute(final GiftCard giftCard) {
        final var entity = repository.save(GiftCardEntityMapper.toEntity(giftCard));
        log.info("giftcard save successfully {}", entity.getId());
    }
}
