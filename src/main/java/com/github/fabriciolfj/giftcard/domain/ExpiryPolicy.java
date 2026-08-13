package com.github.fabriciolfj.giftcard.domain;

import java.time.Period;

public record ExpiryPolicy(String ref, Period duration) { }