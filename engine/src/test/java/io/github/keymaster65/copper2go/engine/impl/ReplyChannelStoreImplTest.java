package io.github.keymaster65.copper2go.engine.impl;

import io.github.keymaster65.copper2go.engine.ReplyChannel;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class ReplyChannelStoreImplTest {

    @Test
    void defaultTest() {
        var store = new ReplyChannelStoreImpl();

        assertThat(store.getReplyChannel("1")).isNull();

        store.store("1", null);
        assertThat(store.getReplyChannel("1")).isNotNull();
        assertThatNoException()
                .isThrownBy(() ->
                        store.reply("1", "Test message"));

        assertThat(store.getReplyChannel("1")).isNull();
        assertThatExceptionOfType(NullPointerException.class)
                .isThrownBy(() ->
                        store.replyError("1", "Test message"));


        store.store("1", null);
        assertThat(store.getReplyChannel("1")).isNotNull();
        assertThatNoException()
                .isThrownBy(() ->
                        store.replyError("1", "Test message"));

    }


    @Test
    void storeAndGet() {
        var store = new ReplyChannelStoreImpl();
        assertThat(store.getReplyChannel("1")).isNull();

        var channel = mock(ReplyChannel.class);
        store.store("1", channel);

        assertThat(store.getReplyChannel("1")).isNotNull().isSameAs(channel);
        }

    @Test
    void reply() {
        var store = new ReplyChannelStoreImpl();
        var channel = mock(ReplyChannel.class);
        store.store("1", channel);

        store.reply("1", "Test message");
        verify(channel).reply("Test message", null);
        verify(channel, times(0)).replyError("Test message", null);
        verify(channel, times(0)).replyError("Test message");

        store.store("1", channel);
        var attributes = mock(Map.class);
        //noinspection unchecked
        store.reply("1", "Test message", attributes);

        //noinspection unchecked
        verify(channel).reply("Test message", attributes);
        verify(channel, times(0)).replyError("Test message", null);
        verify(channel, times(0)).replyError("Test message");
   }

    @Test
    void replyError() {
        var store = new ReplyChannelStoreImpl();
        var channel = mock(ReplyChannel.class);
        store.store("1", channel);

        store.replyError("1", "Test message");

        verify(channel).replyError("Test message", null);
        verify(channel, times(0)).reply("Test message", null);
        verify(channel, times(0)).reply("Test message");


        store.store("1", channel);
        var attributes = mock(Map.class);
        //noinspection unchecked
        store.replyError("1", "Test message", attributes);

        //noinspection unchecked
        verify(channel).replyError("Test message", attributes);
        verify(channel, times(0)).reply("Test message", null);
        verify(channel, times(0)).reply("Test message");
    }
}