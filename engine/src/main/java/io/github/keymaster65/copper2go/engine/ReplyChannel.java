package io.github.keymaster65.copper2go.engine;

public interface ReplyChannel {
    void reply(String message);
    void replyError(String message);
}
