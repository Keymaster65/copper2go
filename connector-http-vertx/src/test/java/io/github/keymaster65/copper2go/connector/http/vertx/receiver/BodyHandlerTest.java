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

import io.github.keymaster65.copper2go.api.connector.EngineException;
import io.github.keymaster65.copper2go.api.connector.PayloadReceiver;
import io.vertx.core.MultiMap;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

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

    // might be refactored if LicenseHandler will be injected as object
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

    // might be refactored if WorkflowHandler will be injected as object
    @Test
    void handleWorkflowRequestV2() throws EngineException {
        final HttpServerResponse response = mock(HttpServerResponse.class);

        handleWorkflowRequest(ApiPath.COPPER2GO_2_API, "request", response);
        verify(response, times(0)).setStatusCode(HttpURLConnection.HTTP_OK);
    }

    // might be refactored if WorkflowHandler will be injected as object
    @Test
    void handleWorkflowRequest() throws EngineException {
        final HttpServerResponse response = mock(HttpServerResponse.class);

        handleWorkflowRequest(ApiPath.COPPER2GO_3_API, "twoway", response);
        verify(response, times(0)).setStatusCode(HttpURLConnection.HTTP_OK);
    }

    // might be refactored if WorkflowHandler will be injected as object
    @Test
    void handleWorkflowEventV2() throws EngineException {
        final HttpServerResponse response = mock(HttpServerResponse.class);
        when(response.setStatusCode(HttpURLConnection.HTTP_ACCEPTED)).thenReturn(response);

        handleWorkflowRequest(ApiPath.COPPER2GO_2_API, "event", response);
        verify(response).setStatusCode(HttpURLConnection.HTTP_ACCEPTED);
    }

    // might be refactored if WorkflowHandler will be injected as object
    @Test
    void handleWorkflowEvent() throws EngineException {
        final HttpServerResponse response = mock(HttpServerResponse.class);
        when(response.setStatusCode(HttpURLConnection.HTTP_ACCEPTED)).thenReturn(response);

        handleWorkflowRequest(ApiPath.COPPER2GO_3_API, "oneway", response);
        verify(response).setStatusCode(HttpURLConnection.HTTP_ACCEPTED);
    }

    void handleWorkflowRequest(final String apiUri, final String type, final HttpServerResponse response) throws EngineException {
        final HttpServerRequest request = mock(HttpServerRequest.class);
        final PayloadReceiver payloadReceiver = mock(PayloadReceiver.class);
        final Buffer buffer = mock(Buffer.class);
        BodyHandler handler = new BodyHandler(request, payloadReceiver);

        final String workflowName = "Hello";
        final long majorVersion = 2L;
        final long minorVersion = 0L;
        when(buffer.getBytes()).thenReturn("Wolf" .getBytes(StandardCharsets.UTF_8));
        when(request.uri()).thenReturn(String.format("%s%s/%d.%d/%s", apiUri, type, majorVersion, minorVersion, workflowName));
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
        when(request.uri()).thenReturn(ApiPath.COPPER2GO_3_API);
        when(request.response()).thenReturn(response);
        when(response.setStatusCode(HttpURLConnection.HTTP_INTERNAL_ERROR)).thenReturn(response);

        handler.handle(buffer);

        verify(response).setStatusCode(HttpURLConnection.HTTP_INTERNAL_ERROR);
        verify(response).end(anyString());
    }

}