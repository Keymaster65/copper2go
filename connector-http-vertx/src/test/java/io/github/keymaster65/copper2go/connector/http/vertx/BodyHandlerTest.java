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

import io.github.keymaster65.copper2go.api.connector.EngineException;
import io.github.keymaster65.copper2go.api.connector.PayloadReceiver;
import io.vertx.core.MultiMap;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import static io.github.keymaster65.copper2go.connector.http.vertx.BodyHandler.COPPER2GO_2_API;
import static java.util.Map.entry;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class BodyHandlerTest {

    @Test
    void createAttributes() {
        MultiMap multiMap = MultiMap.caseInsensitiveMultiMap();
        multiMap.add("a", "A");
        Assertions.assertThat(BodyHandler.createAttributes(multiMap))
                .containsOnly(entry("a", "A"))
                .hasSize(1);
    }

    @Test
    void createAttributesDouble() {
        MultiMap multiMap = MultiMap.caseInsensitiveMultiMap();
        multiMap.add("a", "A");
        multiMap.add("a", "A");
        Assertions.assertThat(BodyHandler.createAttributes(multiMap))
                .containsOnly(entry("a", "A"))
                .hasSize(1);
    }

    @Test
    void createAttributesEmpty() {
        MultiMap multiMap = MultiMap.caseInsensitiveMultiMap();

        Assertions.assertThat(BodyHandler.createAttributes(multiMap)).isEmpty();
    }

    @Test
    void createAttributesNull() {
        Assertions.assertThat(BodyHandler.createAttributes(null)).isEmpty();
    }

    @ParameterizedTest
    @ValueSource(strings = {"/", "/.", "/notFound.html"})
    void handleLicenseNotFound(final String path) {
        HttpServerResponse response = mock(HttpServerResponse.class);
        when(response.setStatusCode(HttpURLConnection.HTTP_NOT_FOUND)).thenReturn(response);

        BodyHandler.handleLicense(response, path);

        verify(response).setStatusCode(HttpURLConnection.HTTP_NOT_FOUND);
        verify(response).end("Exception while getting licenses from uri %s. null" .formatted(path));
    }

    @Test
    void handleLicenseOk() {
        HttpServerResponse response = mock(HttpServerResponse.class);
        when(response.setStatusCode(HttpURLConnection.HTTP_OK)).thenReturn(response);

        BodyHandler.handleLicense(response, "/test.html");

        verify(response).setStatusCode(HttpURLConnection.HTTP_OK);
        verify(response).end(anyString());
    }

    @Test
    void handleLicense() {
        final HttpServerResponse response = mock(HttpServerResponse.class);
        final HttpServerRequest request = mock(HttpServerRequest.class);
        final PayloadReceiver payloadReceiver = mock(PayloadReceiver.class);
        final Buffer buffer = mock(Buffer.class);
        BodyHandler handler = new BodyHandler(request, payloadReceiver);

        when(buffer.getBytes()).thenReturn("Wolf" .getBytes(StandardCharsets.UTF_8));
        when(request.uri()).thenReturn("/test.html");
        when(request.response()).thenReturn(response);
        when(response.setStatusCode(HttpURLConnection.HTTP_OK)).thenReturn(response);

        handler.handle(buffer);

        verify(response).setStatusCode(HttpURLConnection.HTTP_OK);
        verify(response).end(anyString());
    }

    @Test
    void handleWorkflowRequest() throws EngineException {
        final HttpServerResponse response = mock(HttpServerResponse.class);

        handleWorkflowRequest("request", response);
        verify(response, times(0)).setStatusCode(HttpURLConnection.HTTP_OK);
    }

    @Test
    void handleWorkflowEvent() throws EngineException {
        final HttpServerResponse response = mock(HttpServerResponse.class);
        when(response.setStatusCode(HttpURLConnection.HTTP_ACCEPTED)).thenReturn(response);

        handleWorkflowRequest("event", response);
        verify(response).setStatusCode(HttpURLConnection.HTTP_ACCEPTED);
    }

    void handleWorkflowRequest(final String type, final HttpServerResponse response) throws EngineException {
        final HttpServerRequest request = mock(HttpServerRequest.class);
        final PayloadReceiver payloadReceiver = mock(PayloadReceiver.class);
        final Buffer buffer = mock(Buffer.class);
        BodyHandler handler = new BodyHandler(request, payloadReceiver);

        final String workflowName = "Hello";
        final long majorVersion = 2L;
        final long minorVersion = 0L;
        when(buffer.getBytes()).thenReturn("Wolf" .getBytes(StandardCharsets.UTF_8));
        when(request.uri()).thenReturn(String.format("%s%s/%d.%d/%s", COPPER2GO_2_API, type, majorVersion, minorVersion, workflowName));
        var multiMap = MultiMap.caseInsensitiveMultiMap();
        final String key = "a";
        final String value = "A";
        multiMap.add(key, value);
        when(request.params()).thenReturn(multiMap);
        when(request.response()).thenReturn(response);


        Map<String, String> attributes = new HashMap<>();
        attributes.put(key, value);

        handler.handle(buffer);

        verify(payloadReceiver).receive(
                any(),
                eq(attributes),
                any(),
                eq(workflowName),
                eq(majorVersion),
                eq(minorVersion)
        );
    }

    @Test
    void handleWorkflowBadUrl() {
        final HttpServerResponse response = mock(HttpServerResponse.class);
        final HttpServerRequest request = mock(HttpServerRequest.class);
        final PayloadReceiver payloadReceiver = mock(PayloadReceiver.class);
        final Buffer buffer = mock(Buffer.class);
        BodyHandler handler = new BodyHandler(request, payloadReceiver);

        when(buffer.getBytes()).thenReturn("Wolf" .getBytes(StandardCharsets.UTF_8));
        when(request.uri()).thenReturn(COPPER2GO_2_API);
        when(request.response()).thenReturn(response);
        when(response.setStatusCode(HttpURLConnection.HTTP_INTERNAL_ERROR)).thenReturn(response);

        handler.handle(buffer);

        verify(response).setStatusCode(HttpURLConnection.HTTP_INTERNAL_ERROR);
        verify(response).end(anyString());
    }

}