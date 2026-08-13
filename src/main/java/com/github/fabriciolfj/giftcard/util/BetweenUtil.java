package com.github.fabriciolfj.giftcard.util;

public class BetweenUtil {

    private BetweenUtil() { }

    public static <T extends Comparable<? super T>> boolean isBetween(T v, T min, T max) {
        return v.compareTo(min) >= 0 && v.compareTo(max) <= 0;
    }
}
