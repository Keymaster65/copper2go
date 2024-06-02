/*
 * Copyright 2021 Wolf Sluyterman van Langeweyde
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.keymaster65.copper2go.engine;

import io.github.keymaster65.copper2go.api.connector.ReplyChannel;
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
        @SuppressWarnings("unchecked") Map<String,String> attributes = mock(Map.class);

        store.reply(UUID, TEST_MESSAGE, attributes);

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
        @SuppressWarnings("unchecked") Map<String,String> attributes = mock(Map.class);

        store.replyError(UUID, TEST_MESSAGE, attributes);

        verify(channel).replyError(TEST_MESSAGE, attributes);
        verify(channel, times(0)).reply(TEST_MESSAGE, null);
        verify(channel, times(0)).reply(TEST_MESSAGE);
    }
}