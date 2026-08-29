package com.github.fabriciolfj.giftcard.domain;

public interface SaveResult {

    record Executed(GiftCardOrder order) implements SaveResult { }
    record Replayed(String responseBody) implements SaveResult { }
}
