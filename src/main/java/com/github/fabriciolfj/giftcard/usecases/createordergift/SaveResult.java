package com.github.fabriciolfj.giftcard.usecases.createordergift;

import com.github.fabriciolfj.giftcard.domain.GiftCardOrder;

public sealed interface SaveResult permits SaveResult.Executed, SaveResult.Replayed {

    record Executed(GiftCardOrder order) implements SaveResult { }

    record Replayed(int status, String body, String location) implements SaveResult { }
}
