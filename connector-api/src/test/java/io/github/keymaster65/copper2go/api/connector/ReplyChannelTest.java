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
package io.github.keymaster65.copper2go.api.connector;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Map;

class ReplyChannelTest {
    public static final String MESSAGE = "message";


    @Test
    void reply() {
        final ReplyChannel mockedReplyChannel = Mockito.mock(ReplyChannel.class);
        final ReplyChannel replyChannel = createReplyChannel(mockedReplyChannel);

        replyChannel.reply(MESSAGE);

        Mockito.verify(mockedReplyChannel).reply(MESSAGE, null);
    }

    @Test
    void replyError() {
        final ReplyChannel mockedReplyChannel = Mockito.mock(ReplyChannel.class);
        final ReplyChannel replyChannel = createReplyChannel(mockedReplyChannel);

        replyChannel.replyError(MESSAGE);

        Mockito.verify(mockedReplyChannel).replyError(MESSAGE, null);
    }

    private ReplyChannel createReplyChannel(final ReplyChannel wrappedReplyChannel) {
        return new ReplyChannel() {

            @Override
            public void reply(final String reply, final Map<String, String> attributes) {
                wrappedReplyChannel.reply(reply, attributes);
            }

            @Override
            public void replyError(final String reply, final Map<String, String> attributes) {
                wrappedReplyChannel.replyError(reply, attributes);
            }
        };
    }
}
