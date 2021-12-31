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

import io.github.keymaster65.copper2go.api.connector.RequestChannel;
import io.github.keymaster65.copper2go.connector.http.Copper2GoHttpClient;
import io.github.keymaster65.copper2go.connector.http.HttpMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class HttpRequestChannel implements RequestChannel {

    private static final Logger log = LoggerFactory.getLogger(HttpRequestChannel.class);

    private final Copper2GoHttpClient httpClient;
    private final HttpMethod httpMethod;

    public HttpRequestChannel(final HttpMethod httpMethod, final Copper2GoHttpClient httpClient) {
        this.httpClient = httpClient;
        this.httpMethod = httpMethod;
    }

    @Override
    public void request(
            final String request,
            final Map<String, String> attributes,
            final String responseCorrelationId
    ) {
        if (attributes != null) {
            log.warn("Ignore attributes {}", attributes);
        }
        httpClient.request(httpMethod, request, responseCorrelationId);
    }

    @Override
    public void close() {
        httpClient.close();
    }
}
