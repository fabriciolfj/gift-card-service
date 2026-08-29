package com.github.fabriciolfj.giftcard.domain;

public sealed interface SaveResult permits SaveResult.Executed, SaveResult.Replayed {

    record Executed(GiftCardOrder order) implements SaveResult { }

    record Replayed(int status, String body, String location) implements SaveResult { }
}
