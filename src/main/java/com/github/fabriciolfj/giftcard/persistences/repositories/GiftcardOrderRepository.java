package com.github.fabriciolfj.giftcard.persistences.repositories;

import com.github.fabriciolfj.giftcard.persistences.entities.GiftcardOrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface GiftcardOrderRepository extends JpaRepository<GiftcardOrderEntity, UUID> {

    @Modifying
    @Query(value = """
            insert into gift_card_order
                (id, amount_cents, currency, status, expiry_policy_ref,
                 purchaser_ref, recipient_name, recipient_email, recipient_message,
                 correlation_id, version)
            values
                (:#{#e.id}, :#{#e.amountCents}, :#{#e.currency},
                 :#{#e.status.name()}, :#{#e.expiryPolicyRef},
                 :#{#e.purchaserRef},
                 :#{#e.recipient?.name}, :#{#e.recipient?.email},
                 :#{#e.recipient?.message},
                 :#{#e.correlationId}, 0)
            """, nativeQuery = true)
    void insert(@Param("e") GiftcardOrderEntity e);
}
