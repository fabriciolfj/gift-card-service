package com.github.fabriciolfj.giftcard.adapters.createcode;

import com.github.fabriciolfj.giftcard.domain.GiftCardCode;
import com.github.fabriciolfj.giftcard.usecases.creategiftcardcode.GiftCardCodeGenerator;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;

import static com.github.fabriciolfj.giftcard.util.ConstantsUtil.ALPHABET;
import static com.github.fabriciolfj.giftcard.util.ConstantsUtil.LENGTH;

@Component
public class SecureRandomGiftCardCodeGenerator implements GiftCardCodeGenerator {

    private static final SecureRandom RANDOM = new SecureRandom();
    private static final int BODY_LENGTH = LENGTH - 1;

    @Override
    public GiftCardCode generate() {
        final var body = new StringBuilder(BODY_LENGTH);

        for (int i = 0; i < BODY_LENGTH; i++) {
            body.append(ALPHABET.charAt(RANDOM.nextInt(ALPHABET.length())));
        }

        return GiftCardCode.fromBody(body.toString());
    }
}
