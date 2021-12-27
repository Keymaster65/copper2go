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