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

import io.github.keymaster65.copper2go.api.connector.DefaultRequestChannelStore;
import io.github.keymaster65.copper2go.api.connector.ResponseReceiver;
import net.jqwik.api.Example;
import org.assertj.core.api.Assertions;
import org.mockito.Mockito;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.LockSupport;

class RequestChannelConfiguratorTest {

    public static final String CHANNEL_NAME = "channelName";
    public static final String RESPONSE_CORRELATION_ID = "responseCorrelationId";
    public static final String REQUEST = "request";

    @Example
    void putHttpRequestChannels() {
        final ResponseReceiver responseReceiver = Mockito.mock(ResponseReceiver.class);
        Assertions.assertThatCode(() ->
                        createDefaultHttpRequestChannelStore(responseReceiver)
                )
                .doesNotThrowAnyException();
    }

    @Example
    void putHttpRequestChannelsNull() {
        Assertions.assertThatCode(() ->
                        RequestChannelConfigurator.putHttpRequestChannels(null, null, null)
                )
                .doesNotThrowAnyException();
    }

    @Example
    void requestError() {
        final ResponseReceiver responseReceiver = Mockito.mock(ResponseReceiver.class);
        final DefaultRequestChannelStore defaultRequestChannelStore = createDefaultHttpRequestChannelStore(responseReceiver);

        defaultRequestChannelStore.request(CHANNEL_NAME, REQUEST, RESPONSE_CORRELATION_ID);
        LockSupport.parkNanos(15L * 1000 * 1000 * 1000);

        Mockito.verify(responseReceiver).receiveError(
                Mockito.eq(RESPONSE_CORRELATION_ID),
                Mockito.startsWith("Failed to resolve 'httpHost'")
        );
    }

    private DefaultRequestChannelStore createDefaultHttpRequestChannelStore(final ResponseReceiver responseReceiver) {
        final DefaultRequestChannelStore defaultRequestChannelStore = new DefaultRequestChannelStore();
        final Map<String, HttpRequestChannelConfig> httpRequestChannelConfigs = new HashMap<>();
        httpRequestChannelConfigs.put(
                CHANNEL_NAME,
                new HttpRequestChannelConfig(
                        "httpHost",
                        0,
                        "/",
                        "GET"
                ));
        RequestChannelConfigurator.putHttpRequestChannels(httpRequestChannelConfigs, responseReceiver, defaultRequestChannelStore);
        return defaultRequestChannelStore;
    }
}