package com.github.fabriciolfj.giftcard.domain;

import org.jspecify.annotations.NonNull;

import java.math.BigDecimal;
import java.math.RoundingMode;

public record Money(long cents, Currency currency) implements Comparable<Money> {

    public Money {
        if (cents < 0) {
            throw new IllegalArgumentException("cent denied value negative");
        }
    }

    public static Money ofCents(long cents) {
        return new Money(cents, Currency.BRL);
    }

    public static Money zero() {
        return new Money(0, Currency.BRL);
    }

    public BigDecimal toDecimal() {
        return BigDecimal.valueOf(cents)
                .movePointLeft(currency.decimalPlaces())
                .setScale(currency.decimalPlaces(), RoundingMode.UNNECESSARY);
    }

    public Money minus(Money other) {
        sameCurrencyMoney(other);
        return new Money(Math.subtractExact(this.cents, other.cents), currency);
    }

    public Money plus(Money other) {
        sameCurrencyMoney(other);
        return new Money(Math.addExact(this.cents, other.cents), currency);
    }

    public boolean isZero() {
        return this.cents == 0;
    }

    public boolean isGreaterThan(Money money) {
        return compareTo(money) > 0;
    }

    public boolean isLessThan(Money money) {
        return compareTo(money) < 0;
    }

    @Override
    public int compareTo(@NonNull Money other) {
        sameCurrencyMoney(other);
        return Long.compare(this.cents, other.cents);
    }

    @Override
    public String toString() {
        return "%s %s".formatted(currency, toDecimal().toPlainString());
    }

    private void sameCurrencyMoney(Money other) {
        if (!other.currency.equals(this.currency)) {
            throw new IllegalArgumentException("currency incompatible %s %s".formatted(other.currency, this.currency));
        }
    }
}
