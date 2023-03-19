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
package io.github.keymaster65.copper2go.connector.http.vertx.receiver;

import io.github.keymaster65.copper2go.api.connector.ResponseReceiver;
import io.github.keymaster65.copper2go.connector.http.Copper2GoHttpClient;
import io.github.keymaster65.copper2go.connector.http.HttpMethod;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpVersion;
import io.vertx.ext.web.client.HttpRequest;
import io.vertx.ext.web.client.HttpResponse;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.WebClientOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class VertxHttpClient implements Copper2GoHttpClient {

    private final String host;
    private final int port;
    private final String uri;
    private final ResponseReceiver responseReceiver;
    private final Vertx vertx;
    private final WebClient client;
    private static final Logger log = LoggerFactory.getLogger(VertxHttpClient.class);

    public VertxHttpClient(
            final String host,
            final int port,
            final String uri,
            final ResponseReceiver responseReceiver
    ) {
        this(host, port, uri, responseReceiver, Vertx.vertx());
    }

    public VertxHttpClient(
            final String host,
            final int port,
            final String uri,
            final ResponseReceiver responseReceiver,
            final Vertx vertx
    ) {
        this(
                host,
                port,
                uri,
                responseReceiver,
                Vertx.vertx(),
                WebClient.create(
                        vertx,
                        new WebClientOptions().setProtocolVersion(HttpVersion.HTTP_2).setHttp2MaxPoolSize(10000)
                )
        );
    }

    public VertxHttpClient(
            final String host,
            final int port,
            final String uri,
            final ResponseReceiver responseReceiver,
            final Vertx vertx,
            final WebClient client
    ) {
        this.host = host;
        this.port = port;
        this.uri = uri;
        this.responseReceiver = responseReceiver;
        this.vertx = vertx;
        this.client = client;
    }

    @Override
    public void request(final HttpMethod httpMethod, final String request, final String responseCorrelationId, Map<String, String> attributes) {
        final HttpRequest<Buffer> httpRequest = createHttpRequest(httpMethod, attributes);

        log.trace("Before request with responseCorrelationId={}.", responseCorrelationId);
        httpRequest
                .sendBuffer(Buffer.buffer(request))
                .onFailure(errorHandler(responseCorrelationId, responseReceiver))
                .onSuccess(successHandler(responseCorrelationId, responseReceiver));
        log.trace("After request with responseCorrelationId={}.", responseCorrelationId);
    }

    @Override
    public void close() {
        client.close();
        vertx.close(asyncResult -> log.info("VertX close. succeeded={}", asyncResult.succeeded()));
    }

    HttpRequest<Buffer> createHttpRequest(final HttpMethod httpMethod, final Map<String, String> attributes) {
        final HttpRequest<Buffer> bufferHttpRequest = client.request(
                io.vertx.core.http.HttpMethod.valueOf(httpMethod.toString()),
                port,
                host,
                uri
        );
        return addQueryParams(
                attributes,
                bufferHttpRequest
        );
    }

    static Handler<HttpResponse<Buffer>> successHandler(
            final String responseCorrelationId,
            final ResponseReceiver responseReceiver
    ) {
        return result -> {
            if (log.isDebugEnabled()) {
                log.debug(String.format("Result=%s", result.bodyAsString()));
            }
            responseReceiver.receive(responseCorrelationId, result.bodyAsString());
        };
    }

    static Handler<Throwable> errorHandler(
            final String responseCorrelationId,
            final ResponseReceiver responseReceiver
    ) {
        return throwable -> {
            if (log.isDebugEnabled()) {
                log.debug(String.format("Failure=%s", throwable.getMessage()));
            }
            responseReceiver.receiveError(responseCorrelationId, throwable.getMessage());
        };
    }

    private static HttpRequest<Buffer> addQueryParams(final Map<String, String> attributes, final HttpRequest<Buffer> request) {
        if (attributes == null || attributes.isEmpty()) {
            return request;
        }
        attributes.forEach(request::addQueryParam);
        return request;
    }
}
