package com.github.fabriciolfj.giftcard.persistences.entities;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;


@Entity
@Table(name = "gift_card_order")
public class GiftcardOrderEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "amount_cents", nullable = false, updatable = false)
    private long amountCents;

    @Column(name = "currency", nullable = false, length = 3, updatable = false)
    private String currency;

    @Column(name = "status", nullable = false, length = 20)
    private String status;

    @Column(name = "expiry_policy_ref", nullable = false, length = 40, updatable = false)
    private String expiryPolicyRef;

    @Column(name = "purchaser_ref", length = 64, updatable = false)
    private String purchaserRef;

    @Embedded
    private RecipientEmbeddable recipient;

    @Column(name = "payment_ref", length = 64)
    private String paymentRef;

    @Column(name = "paid_amount_cents")
    private Long paidAmountCents;

    @Column(name = "paid_at")
    private Instant paidAt;

    @Column(name = "gift_card_id")
    private UUID giftCardId;

    @Column(name = "cancel_reason", length = 280)
    private String cancelReason;

    @Column(name = "correlation_id", length = 64)
    private String correlationId;

    @Version
    @Column(name = "version", nullable = false)
    private long version;

    @Column(name = "created_at", nullable = false, insertable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false, insertable = false, updatable = false)
    private Instant updatedAt;

    @Column(name = "activated_at")
    private Instant activatedAt;

    @Column(name = "cancelled_at")
    private Instant cancelledAt;

    protected GiftcardOrderEntity() {

    }

    GiftcardOrderEntity(UUID id,
                        long amountCents,
                        String currency,
                        String status,
                        String expiryPolicyRef,
                        String purchaserRef,
                        RecipientEmbeddable recipient,
                        String correlationId) {
        this.id = id;
        this.amountCents = amountCents;
        this.currency = currency;
        this.status = status;
        this.expiryPolicyRef = expiryPolicyRef;
        this.purchaserRef = purchaserRef;
        this.recipient = recipient;
        this.correlationId = correlationId;
    }

    public UUID getId() { return id; }
    public long getAmountCents() { return amountCents; }
    public String getCurrency() { return currency; }
    public String getStatus() { return status; }
    public String getExpiryPolicyRef() { return expiryPolicyRef; }
    public String getPurchaserRef() { return purchaserRef; }
    public RecipientEmbeddable getRecipient() { return recipient; }
    public String getPaymentRef() { return paymentRef; }
    public Long getPaidAmountCents() { return paidAmountCents; }
    public Instant getPaidAt() { return paidAt; }
    public UUID getGiftCardId() { return giftCardId; }
    public String getCancelReason() { return cancelReason; }
    public String getCorrelationId() { return correlationId; }
    public long getVersion() { return version; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public Instant getActivatedAt() { return activatedAt; }
    public Instant getCancelledAt() { return cancelledAt; }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GiftcardOrderEntity other)) return false;
        return id != null && id.equals(other.id);
    }


    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "GiftCardOrderEntity{id=%s, amountCents=%d, status=%s}"
                .formatted(id, amountCents, status);
    }
}