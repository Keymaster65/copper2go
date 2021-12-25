package io.github.keymaster65.copper2go.workflowapi;

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