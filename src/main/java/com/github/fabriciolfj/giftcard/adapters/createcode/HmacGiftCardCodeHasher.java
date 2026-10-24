package com.github.fabriciolfj.giftcard.adapters.createcode;

import com.github.fabriciolfj.giftcard.domain.GiftCardCode;
import com.github.fabriciolfj.giftcard.usecases.creategiftcardcode.GiftCardCodeHasher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

@Component
public class HmacGiftCardCodeHasher implements GiftCardCodeHasher {

    private static final String ALGORITHM = "HmacSHA256";
    private static final int MIN_PEPPER_LENGTH = 32;

    private final SecretKeySpec key;

    public HmacGiftCardCodeHasher(@Value("${giftcard.code.pepper}") final String pepper) {
        if (pepper == null || pepper.length() < MIN_PEPPER_LENGTH) {
            throw new IllegalArgumentException("pepper must be at least " + MIN_PEPPER_LENGTH + " characters long");
        }

        this.key = new SecretKeySpec(pepper.getBytes(StandardCharsets.UTF_8), ALGORITHM);
    }


    @Override
    public String hash(GiftCardCode code) {
        try {
            final var mac = Mac.getInstance(ALGORITHM);
            mac.init(this.key);

            return HexFormat.of().formatHex(mac.doFinal(code.value().getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new IllegalArgumentException("error hashing gift card code", e);
        }
    }

    @Override
    public boolean matches(GiftCardCode code, String storedHash) {
        if (storedHash == null) {
            return false;
        }

        return MessageDigest
                .isEqual(hash(code).getBytes(StandardCharsets.UTF_8),
                        storedHash.getBytes(StandardCharsets.UTF_8));
    }
}
