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

import com.google.common.io.CharStreams;
import io.github.keymaster65.copper2go.connectorapi.EngineException;
import io.github.keymaster65.copper2go.connectorapi.PayloadReceiver;
import io.github.keymaster65.copper2go.connectorapi.WorkflowVersion;
import io.vertx.core.Handler;
import io.vertx.core.MultiMap;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class BodyHandler implements Handler<Buffer> {

    public static final String COPPER2GO_2_API = "/copper2go/2/api/";

    private final HttpServerRequest request;
    private final PayloadReceiver payloadReceiver;

    private static final Logger log = LoggerFactory.getLogger(BodyHandler.class);

    BodyHandler(final HttpServerRequest request, final PayloadReceiver payloadReceiver) {
        this.request = request;
        this.payloadReceiver = payloadReceiver;
    }

    @Override
    public void handle(final Buffer buffer) {
        final String requestBody;
        requestBody = new String(buffer.getBytes(), StandardCharsets.UTF_8);
        final HttpServerResponse response = request.response();
        Map<String, String> attributes = createAttributes(request.params());
        final String uri = request.uri();
        handleBody(requestBody, response, attributes, uri);
    }

    void handleBody(final String requestBody, final HttpServerResponse response, final Map<String, String> attributes, final String uri) {
        if (uri.startsWith(BodyHandler.COPPER2GO_2_API)) {
            handleWorkflow(requestBody, response, attributes, uri);
        } else {
            BodyHandler.handleLicense(response, uri);
        }
    }

    void handleWorkflow(final String requestBody, final HttpServerResponse response, final Map<String, String> attributes, final String uri) {
        try {
            if (uri.startsWith(BodyHandler.COPPER2GO_2_API + "request/") || uri.startsWith(BodyHandler.COPPER2GO_2_API + "event/")) {
                var workflowVersion = WorkflowVersion.of(uri);
                log.debug("Call Workflow on engine {}.", payloadReceiver);
                payloadReceiver.receive(
                        requestBody,
                        attributes,
                        new HttpReplyChannelImpl(response),
                        workflowVersion.name,
                        workflowVersion.major,
                        workflowVersion.minor
                );
            } else {
                throw new IllegalArgumentException(String.format("PATH %s not as expected.", uri));
            }

            if (uri.startsWith(BodyHandler.COPPER2GO_2_API + "event/")) {
                log.debug("Empty OK response for incoming event.");
                response
                        .setStatusCode(HttpURLConnection.HTTP_ACCEPTED)
                        .end();
            }
        } catch (EngineException|RuntimeException e) {
            log.warn("Exception while calling workflow.", e);
            response
                    .setStatusCode(HttpURLConnection.HTTP_INTERNAL_ERROR)
                    .end(String.format("Exception: %s", e.getMessage()));
        }
    }

    public static void handleLicense(final HttpServerResponse response, final String uri) {
        try {
            String path;
            if ("/".equals(uri) || "/.".equals(uri)) {
                path = "license/index.html";
            } else {
                path = "license" + uri;
            }

            try (Reader reader = new InputStreamReader(Objects.requireNonNull(ClassLoader.getSystemClassLoader().getResourceAsStream(path)), StandardCharsets.UTF_8)) {
                response
                        .setStatusCode(HttpURLConnection.HTTP_OK)
                        .end(CharStreams.toString(reader));
            }
        } catch (Exception e) {
            response
                    .setStatusCode(HttpURLConnection.HTTP_NOT_FOUND)
                    .end(String.format("Exception while getting licenses from uri %s. %s", uri, e.getMessage()));
        }
    }

    static Map<String, String> createAttributes(final MultiMap params) {
        if (params == null || params.isEmpty()) {
            return Map.of();
        }
        var attributes = new HashMap<String, String>();
        params.iterator().forEachRemaining(entry -> attributes.put(entry.getKey(), entry.getValue()));
        return attributes;
    }


}
