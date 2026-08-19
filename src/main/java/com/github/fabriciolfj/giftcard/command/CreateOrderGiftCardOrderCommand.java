package com.github.fabriciolfj.giftcard.command;

import lombok.Builder;

@Builder
public record CreateOrderGiftCardOrderCommand(Long amountCents, String purcheaserRef, RecipientCommand recipientCommand) implements IdempotentCommand {


    private static final String VERSION = "v1";
    private static final String SEP = "\u001F";
    private static final String NULL = "\u0000";

    @Override
    public String canonicalForm() {
        return String.join(SEP,
                VERSION,
                String.valueOf(amountCents),
                nz(purcheaserRef),
                nz(recipientCommand == null ? null : recipientCommand.name()),
                nz(recipientCommand == null ? null : recipientCommand.email()),
                nz(recipientCommand == null ? null : recipientCommand.message()));
    }

    private static String nz(String s) {
        return s == null ? NULL : s;
    }
}
