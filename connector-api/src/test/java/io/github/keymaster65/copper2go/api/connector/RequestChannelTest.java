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

class RequestChannelTest {

    public static final String REQUEST = "request";
    public static final String RESPONSE_CORRELATION_ID = "responseCorrelationId";

    @Test
    void request() {
        final RequestChannel mockedRequestChannel = Mockito.mock(RequestChannel.class);
        final RequestChannel requestChannel = createRequestChannel(mockedRequestChannel);

        requestChannel.request(REQUEST, RESPONSE_CORRELATION_ID);

        Mockito.verify(mockedRequestChannel).request(REQUEST, null, RESPONSE_CORRELATION_ID);

    }

    private RequestChannel createRequestChannel(final RequestChannel wrappedRequestChannel) {
        return new RequestChannel() {

            @Override
            public void request(final String request, final Map<String, String> attributes, final String responseCorrelationId) {
                wrappedRequestChannel.request(request, attributes, responseCorrelationId);
            }

            @Override
            public void close() {
                wrappedRequestChannel.close();
            }
        };
    }
}
