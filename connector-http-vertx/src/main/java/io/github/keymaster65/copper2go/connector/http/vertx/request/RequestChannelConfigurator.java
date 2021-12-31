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
import io.github.keymaster65.copper2go.connector.http.vertx.receiver.VertxHttpClient;

import java.util.Map;

public class RequestChannelConfigurator {

    private RequestChannelConfigurator() {}

    public static void putHttpRequestChannels(
            final Map<String, HttpRequestChannelConfig> httpRequestChannelConfigs,
            final ResponseReceiver responseReceiver,
            final DefaultRequestChannelStore defaultRequestChannelStore

    ) {
        if (httpRequestChannelConfigs != null) {
            for (Map.Entry<String, HttpRequestChannelConfig> entry : httpRequestChannelConfigs.entrySet()) {
                HttpRequestChannelConfig config = entry.getValue();
                defaultRequestChannelStore.put(entry.getKey(),
                        new HttpRequestChannel(
                                config.method,
                                new VertxHttpClient(
                                        config.host,
                                        config.port,
                                        config.path,
                                        responseReceiver
                                )));
            }
        }
    }
}
