package com.github.fabriciolfj.giftcard.domain;

import java.time.Instant;

public record PeriodRequest(Instant dateRequest, Instant periodValid) {
}
