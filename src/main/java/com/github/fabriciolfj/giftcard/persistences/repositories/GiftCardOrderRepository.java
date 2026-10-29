package com.github.fabriciolfj.giftcard.persistences.repositories;

import com.github.fabriciolfj.giftcard.persistences.entities.GiftCardOrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.UUID;

public interface GiftCardOrderRepository extends JpaRepository<GiftCardOrderEntity, UUID> {

    @Modifying
    @Query(value = """
            insert into gift_card_order
                (id, amount_cents, currency, status, expiry_policy_ref,
                 purchaser_ref, recipient_name, recipient_email, recipient_message,
                 correlation_id, version)
            values
                (:#{#e.id}, :#{#e.amountCents}, :#{#e.currency},
                 :#{#e.status}, :#{#e.expiryPolicyRef},
                 :#{#e.purchaserRef},
                 :#{#e.recipient?.name}, :#{#e.recipient?.email},
                 :#{#e.recipient?.message},
                 :#{#e.correlationId}, 0)
            """, nativeQuery = true)
    void insert(@Param("e") GiftCardOrderEntity e);

    @Modifying
    @Query("""
        update GiftCardOrderEntity o
           set o.status = 'ACTIVATED',
               o.giftCardId = :giftCardId,
               o.paymentRef = :paymentRef,
               o.paidAmountCents = :paidAmountCents,
               o.paidAt = :paidAt,
               o.activatedAt = :activatedAt
         where o.id = :id
           and o.status = 'PENDING_PAYMENT'
        """)
    int activate(@Param("id") UUID id,
                 @Param("giftCardId") UUID giftCardId,
                 @Param("paymentRef") String paymentRef,
                 @Param("paidAmountCents") Long paidAmountCents,
                 @Param("paidAt") Instant paidAt,
                 @Param("activatedAt") Instant activatedAt);

}
