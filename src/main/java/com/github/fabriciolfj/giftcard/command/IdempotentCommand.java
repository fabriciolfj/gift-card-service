package com.github.fabriciolfj.giftcard.command;

public interface IdempotentCommand {

    String canonicalForm();
}
