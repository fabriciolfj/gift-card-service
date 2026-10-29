package com.github.fabriciolfj.giftcard.persistences.repositories;

import com.github.fabriciolfj.giftcard.persistences.entities.GiftCardEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface GiftCardRepository extends JpaRepository<GiftCardEntity, UUID> {
}
