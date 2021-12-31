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
import io.vertx.core.http.HttpServerResponse;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.net.HttpURLConnection;
import java.util.Map;

import static org.mockito.Mockito.mock;

class WorkflowHandlerTest {

    public static final String BODY = "body";

    @Test
    void pathNotExpected() {
        final HttpServerResponse response = mock(HttpServerResponse.class);
        Mockito.when(response.setStatusCode(HttpURLConnection.HTTP_INTERNAL_ERROR)).thenReturn(response);
        final PayloadReceiver payloadReceiver = mock(PayloadReceiver.class);

        WorkflowHandler.handleWorkflow(
                BODY,
                response,
                Map.of(),
                "badUri",
                payloadReceiver
        );

        Mockito.verify(response).end("Exception: PATH badUri not as expected.");
    }

    @Test
    void twoway() throws EngineException {
        final HttpServerResponse response = mock(HttpServerResponse.class);
        final PayloadReceiver payloadReceiver = mock(PayloadReceiver.class);

        final Map<String, String> attributes = Map.of();
        WorkflowHandler.handleWorkflow(
                BODY,
                response,
                attributes,
                ApiPath.TWOWAY_PATH + "2.0/Hello",
                payloadReceiver
        );

        Mockito.verify(response, Mockito.never()).end();
        Mockito.verify(payloadReceiver).receive (
                Mockito.eq(BODY),
                Mockito.eq(attributes),
                Mockito.any(),
                Mockito.eq("Hello"),
                Mockito.eq(2L),
                Mockito.eq(0L)
        );
    }


    @Test
    void oneway() throws EngineException {
        final HttpServerResponse response = mock(HttpServerResponse.class);
        Mockito.when(response.setStatusCode(HttpURLConnection.HTTP_ACCEPTED)).thenReturn(response);

        final PayloadReceiver payloadReceiver = mock(PayloadReceiver.class);

        final Map<String, String> attributes = Map.of();
        WorkflowHandler.handleWorkflow(
                BODY,
                response,
                attributes,
                ApiPath.ONEWAY_PATH + "2.0/Hello",
                payloadReceiver
        );

        Mockito.verify(response).end();
        Mockito.verify(payloadReceiver).receive (
                Mockito.eq(BODY),
                Mockito.eq(attributes),
                Mockito.any(),
                Mockito.eq("Hello"),
                Mockito.eq(2L),
                Mockito.eq(0L)
        );
    }
}