package com.github.fabriciolfj.giftcard.adapters.getgiftcardorder;

import com.github.fabriciolfj.giftcard.domain.GiftCardOrder;
import com.github.fabriciolfj.giftcard.persistences.entities.GiftCardEntityMapper;
import com.github.fabriciolfj.giftcard.persistences.entities.GiftCardOrderEntityMapper;
import com.github.fabriciolfj.giftcard.persistences.repositories.GiftCardOrderRepository;
import com.github.fabriciolfj.giftcard.usecases.common.GetGiftCardOrderGateway;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Component
public class GetGiftCardOrderAdapter implements GetGiftCardOrderGateway {

    private final GiftCardOrderRepository repository;

    @Override
    public Optional<GiftCardOrder> findByOrder(UUID uuid) {
        return repository.findById(uuid)
                .map(GiftCardOrderEntityMapper::toDomain);
    }
}
