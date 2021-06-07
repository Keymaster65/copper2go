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

    public static final String UUID = "1";
    public static final String TEST_MESSAGE = "Test message";

    @Test
    void defaultTest() {
        var store = new ReplyChannelStoreImpl();

        assertThat(store.getReplyChannel(UUID)).isNull();

        store.store(UUID, null);
        assertThat(store.getReplyChannel(UUID)).isNotNull();
        assertThatNoException()
                .isThrownBy(() ->
                        store.reply(UUID, TEST_MESSAGE));

        assertThat(store.getReplyChannel(UUID)).isNull();
        assertThatExceptionOfType(NullPointerException.class)
                .isThrownBy(() ->
                        store.replyError(UUID, TEST_MESSAGE));


        store.store(UUID, null);
        assertThat(store.getReplyChannel(UUID)).isNotNull();
        assertThatNoException()
                .isThrownBy(() ->
                        store.replyError(UUID, TEST_MESSAGE));

    }


    @Test
    void storeAndGet() {
        var store = new ReplyChannelStoreImpl();
        assertThat(store.getReplyChannel(UUID)).isNull();

        var channel = mock(ReplyChannel.class);
        store.store(UUID, channel);

        assertThat(store.getReplyChannel(UUID)).isNotNull().isSameAs(channel);
        }

    @Test
    void reply() {
        var store = new ReplyChannelStoreImpl();
        var channel = mock(ReplyChannel.class);
        store.store(UUID, channel);

        store.reply(UUID, TEST_MESSAGE);
        verify(channel).reply(TEST_MESSAGE, null);
        verify(channel, times(0)).replyError(TEST_MESSAGE, null);
        verify(channel, times(0)).replyError(TEST_MESSAGE);

        store.store(UUID, channel);
        var attributes = mock(Map.class);
        //noinspection unchecked
        store.reply(UUID, TEST_MESSAGE, attributes);

        //noinspection unchecked
        verify(channel).reply(TEST_MESSAGE, attributes);
        verify(channel, times(0)).replyError(TEST_MESSAGE, null);
        verify(channel, times(0)).replyError(TEST_MESSAGE);
   }

    @Test
    void replyError() {
        var store = new ReplyChannelStoreImpl();
        var channel = mock(ReplyChannel.class);
        store.store(UUID, channel);

        store.replyError(UUID, TEST_MESSAGE);

        verify(channel).replyError(TEST_MESSAGE, null);
        verify(channel, times(0)).reply(TEST_MESSAGE, null);
        verify(channel, times(0)).reply(TEST_MESSAGE);


        store.store(UUID, channel);
        var attributes = mock(Map.class);
        //noinspection unchecked
        store.replyError(UUID, TEST_MESSAGE, attributes);

        //noinspection unchecked
        verify(channel).replyError(TEST_MESSAGE, attributes);
        verify(channel, times(0)).reply(TEST_MESSAGE, null);
        verify(channel, times(0)).reply(TEST_MESSAGE);
    }
}