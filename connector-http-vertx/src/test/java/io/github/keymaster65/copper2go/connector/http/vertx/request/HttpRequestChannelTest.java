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
package io.github.keymaster65.copper2go.connector.http.vertx.request;

import io.github.keymaster65.copper2go.connector.http.Copper2GoHttpClient;
import io.github.keymaster65.copper2go.connector.http.HttpMethod;
import net.jqwik.api.Arbitraries;
import net.jqwik.api.Arbitrary;
import net.jqwik.api.Example;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.Provide;
import net.jqwik.api.constraints.WithNull;
import org.mockito.Mockito;

import java.util.Map;


class HttpRequestChannelTest {

    public static final String REQUEST = "request";
    public static final String RESPONSE_CORRELATION_ID = "responseCorrelationId";

    @SuppressWarnings("unused")
    @Provide
    private Arbitrary<Map<String, String>> attributes() {
        return Arbitraries.of(
                Map.of(),
                Map.of("key", "value")
        );
    }

    @Property
    void request(@ForAll final HttpMethod httpMethod, @ForAll("attributes") @WithNull final Map<String, String> attributes) {
        final Copper2GoHttpClient httpClient = Mockito.mock(Copper2GoHttpClient.class);
        final HttpRequestChannel httpRequestChannel = new HttpRequestChannel(
                httpMethod,
                httpClient
        );

        httpRequestChannel.request(
                REQUEST,
                attributes,
                RESPONSE_CORRELATION_ID
        );

        Mockito.verify(httpClient).request(httpMethod, REQUEST, RESPONSE_CORRELATION_ID);
    }

    @Example
    void close() {
        final Copper2GoHttpClient httpClient = Mockito.mock(Copper2GoHttpClient.class);
        final HttpMethod httpMethod = HttpMethod.GET;
        final HttpRequestChannel httpRequestChannel = new HttpRequestChannel(
                httpMethod,
                httpClient
        );

        httpRequestChannel.close();

        Mockito.verify(httpClient).close();
    }
}