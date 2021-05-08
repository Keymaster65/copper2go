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
import io.github.keymaster65.copper2go.engine.Copper2GoEngine;
import io.github.keymaster65.copper2go.engine.EngineException;
import io.github.keymaster65.copper2go.engine.WorkflowVersion;
import io.vertx.core.Handler;
import io.vertx.core.MultiMap;
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

public class RequestHandler implements Handler<HttpServerRequest> {

    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);
    public static final String COPPER2GO_2_API = "/copper2go/2/api/";

    private final Copper2GoEngine copper2GoEngine;

    public RequestHandler(final Copper2GoEngine copper2GoEngine) {
        this.copper2GoEngine = copper2GoEngine;
    }

    static Map<String, String> createAttributes(final MultiMap params) {
        if (params == null || params.isEmpty()) {
            return null;
        }
        var attributes = new HashMap<String, String>();
        params.iterator().forEachRemaining(entry -> attributes.put(entry.getKey(), entry.getValue()));
        return attributes;
    }

    @Override
    public void handle(final HttpServerRequest request) {
        request.bodyHandler(buffer -> {
            final String requestBody;
            requestBody = new String(buffer.getBytes(), StandardCharsets.UTF_8);
            final HttpServerResponse response = request.response();
            Map<String, String> attributes = createAttributes(request.params());
            final String uri = request.uri();
            handleBody(requestBody, response, attributes, uri);
        });
    }

    private void handleBody(final String requestBody, final HttpServerResponse response, final Map<String, String> attributes, final String uri) {
        if (uri.length() > 1 && uri.startsWith(COPPER2GO_2_API)) {
            handleWorkflow(requestBody, response, attributes, uri);
        } else {
            handleLicense(response, uri);
        }
    }

    private void handleWorkflow(final String requestBody, final HttpServerResponse response, final Map<String, String> attributes, final String uri) {
        try {
            if (uri.startsWith(COPPER2GO_2_API + "request/") || uri.startsWith(COPPER2GO_2_API + "event/")) {
                WorkflowVersion workflowVersion = WorkflowVersion.of(uri);
                copper2GoEngine.callWorkflow(
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

            if (uri.startsWith(COPPER2GO_2_API + "event/")) {
                log.debug("Empty OK response for incoming event.");
                response
                        .setStatusCode(HttpURLConnection.HTTP_ACCEPTED)
                        .end();
            }
        } catch (EngineException e) {
            response
                    .setStatusCode(HttpURLConnection.HTTP_INTERNAL_ERROR)
                    .end(String.format("Exception: %s", e.getMessage()));
            log.warn("Exception while calling workflow.", e);
        }
    }

    private void handleLicense(final HttpServerResponse response, final String uri) {
        try {
            String path;
            if ("/".equals(uri) || "/.".equals(uri)) {
                path = "license/index.html";
            } else {
                path = "license" + uri;
            }

            try (Reader reader = new InputStreamReader(Objects.requireNonNull(ClassLoader.getSystemClassLoader().getResourceAsStream(path)), StandardCharsets.UTF_8)) {
                response
                        .setStatusCode(HttpURLConnection.HTTP_NOT_FOUND)
                        .end(CharStreams.toString(reader));
            }
        } catch (Exception e) {
            response.end(String.format("Exception while getting licenses from uri %s. %s", uri, e.getMessage()));
        }
    }
}
