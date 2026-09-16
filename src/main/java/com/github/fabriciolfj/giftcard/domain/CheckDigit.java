package com.github.fabriciolfj.giftcard.domain;

import static com.github.fabriciolfj.giftcard.util.ConstantsUtil.ALPHABET;
import static com.github.fabriciolfj.giftcard.util.ConstantsUtil.LENGTH;

final class CheckDigit {

    private static final int BASE = 32;

    private CheckDigit() {
    }

    static char compute(final String body) {
        int sum = 0;

        for (int i = 0; i < body.length(); i++) {
            final int value = ALPHABET.indexOf(body.charAt(i));
            sum += (i % 2 == 0) ? value : value * 3;
        }

        return ALPHABET.charAt(Math.floorMod(-sum, BASE));
    }

    static boolean isValid(final String full) {
        final var body = full.substring(0, LENGTH - 1);
        return compute(body) == full.charAt(LENGTH - 1);
    }
}