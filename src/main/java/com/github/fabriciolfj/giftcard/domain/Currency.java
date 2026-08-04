package com.github.fabriciolfj.giftcard.domain;

public enum Currency {
    BRL(2);

    private final int decimalPlaces;

    Currency(int decimalPlaces) { this.decimalPlaces = decimalPlaces; }

    public int decimalPlaces() { return decimalPlaces; }
}