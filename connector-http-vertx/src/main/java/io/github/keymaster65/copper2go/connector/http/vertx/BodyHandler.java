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

import io.github.keymaster65.copper2go.api.connector.PayloadReceiver;
import io.vertx.core.Handler;
import io.vertx.core.MultiMap;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class BodyHandler implements Handler<Buffer> {

    private final HttpServerRequest request;
    private final PayloadReceiver payloadReceiver;

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
        if (ApiPath.isApiUri(uri)) {
            ApiPath.logIfDeprecatedApiUri(uri);
            WorkflowHandler.handleWorkflow(requestBody, response, attributes, uri, payloadReceiver);
        } else {
            LicenseHandler.handleLicense(response, uri);
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
