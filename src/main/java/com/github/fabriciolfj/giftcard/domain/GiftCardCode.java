package com.github.fabriciolfj.giftcard.domain;

import java.util.Objects;

import static com.github.fabriciolfj.giftcard.util.ConstantsUtil.ALPHABET;
import static com.github.fabriciolfj.giftcard.util.ConstantsUtil.LENGTH;

public final class GiftCardCode {

    private final String value;

    private GiftCardCode(final String value) {
        this.value = value;
    }

    public static GiftCardCode of(final String input) {
        Objects.requireNonNull(input, "value required");

        final var code = normaliza(input);
        valideCode(code);
        valideAlphabet(code);
        valideCheckDigit(code);

        return new GiftCardCode(code);
    }


    public static GiftCardCode fromBody(final String body) {
        if (body.length() != LENGTH - 1) {
            throw new IllegalArgumentException("body invalid length");
        }
        return of(body + CheckDigit.compute(body));
    }

    public static boolean isWellFormed(final String input) {
        try {
            of(input);
            return true;
        } catch (IllegalArgumentException | NullPointerException e) {
            return false;
        }
    }

    private static String normaliza(final String code) {
        var value = code.toUpperCase();
        var sb = new StringBuilder(LENGTH);
        for (var c : value.toCharArray()) {
            switch(c) {
                case ' ', '-', '\t' -> { }
                case 'L', 'I' -> sb.append("1");
                case 'O' -> sb.append("0");
                default -> sb.append(c);
            }
        }

        return sb.toString();
    }

    private static void valideCode(final String code) {
        if (code.length() != LENGTH) {
            throw new IllegalArgumentException("value invalid");
        }
    }

    private static void valideAlphabet(final String code) {
        if (!code.chars().allMatch(c -> ALPHABET.indexOf(c) >= 0)) {
            throw new IllegalArgumentException("value the code not found alphabet");
        }
    }

    private static void valideCheckDigit(final String code) {
        if (!CheckDigit.isValid(code)) {
            throw new IllegalArgumentException("check digit invalid");
        }
    }

    public String value() {
        return value;
    }

    public String last4() {
        return value.substring(LENGTH - 4);
    }

    public String formatted() {
        return value.replaceAll("(.{4})(?=.)", "$1-");
    }

    @Override
    public boolean equals(final Object o) {
        return o instanceof GiftCardCode other && value.equals(other.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    /**
     * Nunca expõe o código: log de objeto é o vazamento mais provável.
     */
    @Override
    public String toString() {
        return "GiftCardCode[****" + last4() + "]";
    }
}