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
package io.github.keymaster65.copper2go.connector.http.vertx;

import io.github.keymaster65.copper2go.connector.http.Copper2GoHttpClient;
import io.github.keymaster65.copper2go.connector.http.HttpMethod;
import io.github.keymaster65.copper2go.engine.Copper2GoEngine;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.ext.web.client.HttpRequest;
import io.vertx.ext.web.client.HttpResponse;
import io.vertx.ext.web.client.WebClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class VertxHttpClient implements Copper2GoHttpClient {

    private final String host;
    private final int port;
    private final String uri;
    private final Copper2GoEngine engine;
    private final Vertx vertx;
    private final WebClient client;
    private static final Logger log = LoggerFactory.getLogger(VertxHttpClient.class);

    public VertxHttpClient(
            final String host,
            final int port,
            final String uri,
            final Copper2GoEngine engine
    ) {
        this(host, port, uri, engine, Vertx.vertx());
    }

    public VertxHttpClient(
            final String host,
            final int port,
            final String uri,
            final Copper2GoEngine engine,
            final Vertx vertx
    ) {
        this(host, port, uri, engine, Vertx.vertx(), WebClient.create(vertx));
    }

    public VertxHttpClient(
            final String host,
            final int port,
            final String uri,
            final Copper2GoEngine engine,
            final Vertx vertx,
            final WebClient client
    ) {
        this.host = host;
        this.port = port;
        this.uri = uri;
        this.engine = engine;
        this.vertx = vertx;
        this.client = client;
    }

    private Handler<HttpResponse<Buffer>> successHandler(final String responseCorrelationId, final Copper2GoEngine engine) {
        return result -> {
            if (log.isTraceEnabled()) {
                log.trace(String.format("Result=%s", result.bodyAsString()));
            }
            engine.notify(responseCorrelationId, result.bodyAsString());
        };
    }

    private Handler<Throwable> errorHandler(final String responseCorrelationId, final Copper2GoEngine engine) {
        return err -> {
            if (log.isTraceEnabled()) {
                log.trace(String.format("Failure=%s", err.getMessage()));
            }
            engine.notifyError(responseCorrelationId, err.getMessage());
        };
    }

    @Override
    public void request(final HttpMethod httpMethod, final String request, final String responseCorrelationId, Map<String, String> attributes) {
        final HttpRequest<Buffer> httpRequest = addQueryParams(
                attributes,
                client.request(io.vertx.core.http.HttpMethod.valueOf(httpMethod.toString()), port, host, uri)
        );

        httpRequest
                .sendBuffer(Buffer.buffer(request))
                .onFailure(errorHandler(responseCorrelationId, engine))
                .onSuccess(successHandler(responseCorrelationId, engine));
    }

    @Override
    public void close() {
        client.close();
        vertx.close(asyncResult -> log.info("VertX close. succeeded={}", asyncResult.succeeded()));
    }

    static HttpRequest<Buffer> addQueryParams(final Map<String, String> attributes, final HttpRequest<Buffer> request) {
        if (attributes == null || attributes.isEmpty()) {
            return request;
        }
        attributes.forEach(request::addQueryParam);
        return request;
    }
}
