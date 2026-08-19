package com.github.fabriciolfj.giftcard.adapters.createordergiftrcard;

import com.github.fabriciolfj.giftcard.command.IdempotentCommand;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

public final class Fingerprint {

    private Fingerprint() { }

    public static String of(final IdempotentCommand command) {
        try {
            final var digest = MessageDigest.getInstance("SHA-256");
            final var hash = digest.digest(
                    command.canonicalForm().getBytes(StandardCharsets.UTF_8)
            );

            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalArgumentException("SHA-256 indisponivel, detalhes " + e.getMessage());
        }
    }
}
