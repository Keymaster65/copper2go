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
package io.github.keymaster65.copper2go.api.workflow;

import io.github.keymaster65.copper2go.api.workflow.ReplyChannelStore;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Map;

class ReplyChannelStoreTest {

    public static final String UUID = "uuid";
    public static final String MESSAGE = "message";

    @Test
    void reply() {
        final ReplyChannelStore mockedReplyChannelStore = Mockito.mock(ReplyChannelStore.class);
        final ReplyChannelStore replyChannelStore = createReplyChannelStore(mockedReplyChannelStore);

        replyChannelStore.reply(UUID, MESSAGE);

        Mockito.verify(mockedReplyChannelStore).reply(UUID, MESSAGE, null);
    }

    @Test
    void replyError() {
        final ReplyChannelStore mockedReplyChannelStore = Mockito.mock(ReplyChannelStore.class);
        final ReplyChannelStore replyChannelStore = createReplyChannelStore(mockedReplyChannelStore);

        replyChannelStore.replyError(UUID, MESSAGE);

        Mockito.verify(mockedReplyChannelStore).replyError(UUID, MESSAGE, null);
    }

    private ReplyChannelStore createReplyChannelStore(final ReplyChannelStore wrappedReplyChannelStore) {
        return new ReplyChannelStore() {

            @Override
            public void reply(final String channelName, final String event, final Map<String, String> attributes) {
                wrappedReplyChannelStore.reply(channelName, event, attributes);
            }

            @Override
            public void replyError (final String channelName, final String event, final Map<String, String> attributes) {
                wrappedReplyChannelStore.replyError(channelName, event, attributes);
            }
        };
    }
}