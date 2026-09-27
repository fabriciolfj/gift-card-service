package com.github.fabriciolfj.giftcard.usecases.creategiftcardcode;

import com.github.fabriciolfj.giftcard.domain.GiftCardCode;

public interface GiftCardCodeHasher {

    String hash(final GiftCardCode code);

    boolean matchs(final GiftCardCode code, final String storedHash);
}
