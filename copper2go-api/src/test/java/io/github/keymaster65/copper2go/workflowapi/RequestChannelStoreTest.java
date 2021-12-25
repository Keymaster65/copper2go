package io.github.keymaster65.copper2go.workflowapi;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class RequestChannelStoreTest {

    public static final String CHANNEL_NAME = "channelName";
    public static final String REQUEST = "request";
    public static final String RESPONSE_CORRELATION_ID = "responseCorrelationId";

    @Test
    void request() {
        final RequestChannelStore mockedRequestChannelStore = Mockito.mock(RequestChannelStore.class);
        final RequestChannelStore requestChannelStore = createRequestChannelStore(mockedRequestChannelStore);

        requestChannelStore.request(CHANNEL_NAME, REQUEST, RESPONSE_CORRELATION_ID);

        Mockito.verify(mockedRequestChannelStore).request(CHANNEL_NAME, REQUEST, null, RESPONSE_CORRELATION_ID);
    }

    private RequestChannelStore createRequestChannelStore(final RequestChannelStore wrappedRequestChannelStore) {
        //noinspection FunctionalExpressionCanBeFolded
        return wrappedRequestChannelStore::request;
    }
}