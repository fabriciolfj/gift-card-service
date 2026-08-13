package com.github.fabriciolfj.giftcard.command;

import lombok.Builder;

@Builder
public record RecipientCommand(String name, String email, String message) {
}
