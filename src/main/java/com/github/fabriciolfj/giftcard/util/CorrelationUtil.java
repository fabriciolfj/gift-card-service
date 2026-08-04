package com.github.fabriciolfj.giftcard.util;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CorrelationUtil {

    private static final Map<String, String> context = new ConcurrentHashMap<>();
    public static String CORRELATION_ID = "correlationId";

    public static void setCorrelationID(String correlationId) {
        context.put(CORRELATION_ID, correlationId);
    }

    public static String current() {
        return context.get(CORRELATION_ID);
    }
}
