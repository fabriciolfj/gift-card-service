package com.github.fabriciolfj.giftcard.persistences.entities;

import com.github.fabriciolfj.giftcard.domain.GiftCardStatus;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

import static jakarta.persistence.EnumType.STRING;

@Table(name = "gift_card")
@Entity
public class GiftcardEntity {
    @Id
    private UUID id;

    @Column(updatable = false)
    private String codeHash;

    @Column(updatable = false)
    private String codeLast4;

    @Enumerated(STRING)
    private GiftCardStatus status;

    @Column(updatable = false)
    private String currency;

    private Instant expiresAt;

    private String blockReason;

    @Version
    private long version;

    @Column(insertable = false, updatable = false)
    private Instant createdAt;

    @Column(insertable = false, updatable = false)
    private Instant updatedAt;

    public GiftcardEntity() {

    }

    public GiftcardEntity(UUID id,
                          String codeHash,
                          String codeLast4,
                          GiftCardStatus status,
                          String currency,
                          Instant expiresAt,
                          String blockReason,
                          long version,
                          Instant createdAt,
                          Instant updatedAt) {
        this.id = id;
        this.codeHash = codeHash;
        this.codeLast4 = codeLast4;
        this.status = status;
        this.currency = currency;
        this.expiresAt = expiresAt;
        this.blockReason = blockReason;
        this.version = version;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public UUID getId() { return id; }
    public String getCodeHash() { return codeHash; }
    public String getCodeLast4() { return codeLast4; }
    public GiftCardStatus getStatus() { return status; }
    public String getCurrency() { return currency; }
    public Instant getExpiresAt() { return expiresAt; }
    public String getBlockReason() { return blockReason; }
    public long getVersion() { return version; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}
