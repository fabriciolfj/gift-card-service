package com.github.fabriciolfj.giftcard.command;

import lombok.Builder;

@Builder
public record ParameterValidationCommand(Long minAmount, Long maxAmount, Long multiple) {
}
