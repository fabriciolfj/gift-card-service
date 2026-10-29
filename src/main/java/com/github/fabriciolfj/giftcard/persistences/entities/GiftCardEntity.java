package com.github.fabriciolfj.giftcard.persistences.entities;

import com.github.fabriciolfj.giftcard.domain.GiftCardStatus;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.UUID;

import static jakarta.persistence.EnumType.STRING;

@Table(name = "gift_card")
@Entity
public class GiftCardEntity {
    @Id
    private UUID id;

    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(updatable = false, length = 64)
    private String codeHash;

    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(updatable = false, length = 4)
    private String codeLast4;

    @Enumerated(STRING)
    private GiftCardStatus status;

    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(updatable = false, length = 3)
    private String currency;

    @Column(name = "expiry_period")
    private Instant expiresAt;

    private String blockReason;

    @Version
    private long version;

    @Column(insertable = false, updatable = false)
    private Instant createdAt;

    @Column(insertable = false, updatable = false)
    private Instant updatedAt;

    public GiftCardEntity() {

    }

    public GiftCardEntity(UUID id,
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
