package com.github.fabriciolfj.giftcard.adapters.createordergiftrcard.replay;

public interface IdempotencyReplayRule {

    void verify(IdempotencyReplayContext context);
}
