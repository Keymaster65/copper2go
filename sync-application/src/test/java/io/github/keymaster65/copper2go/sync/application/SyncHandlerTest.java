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
package io.github.keymaster65.copper2go.sync.application;

import com.sun.net.httpserver.HttpExchange;
import io.github.keymaster65.copper2go.engine.sync.workflowapi.Workflow;
import io.github.keymaster65.copper2go.engine.sync.workflowapi.WorkflowData;
import io.github.keymaster65.copper2go.engine.sync.workflowapi.WorkflowFactory;
import net.jqwik.api.Arbitraries;
import net.jqwik.api.Arbitrary;
import net.jqwik.api.Example;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.Provide;
import org.assertj.core.api.Assertions;
import org.mockito.Mockito;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;

class SyncHandlerTest {

    @Provide
    Arbitrary<String> workflows() {
        return Arbitraries.of(
                "Hello",
                "Pricing"
        );
    }

    @Property
    void handleHello(
            @ForAll("workflows") final String workflowName,
            @ForAll final String payload
    ) throws IOException, URISyntaxException {
        final WorkflowFactory workflowFactory = Mockito.mock(WorkflowFactory.class);
        final Workflow workflow = Mockito.mock(Workflow.class);
        Mockito
                .when(workflowFactory.create(Mockito.eq(workflowName), Mockito.anyLong(), Mockito.eq(0L)))
                .thenReturn(workflow);
        Mockito
                .when(workflow.main(Mockito.any()))
                .thenAnswer(workflowData -> ((WorkflowData) workflowData.getArguments()[0]).getPayload());
        final SyncHandler syncHandler = new SyncHandler(workflowFactory);

        final HttpExchange httpExchange = Mockito.mock(HttpExchange.class);
        Mockito.when(httpExchange.getRequestURI()).thenReturn(new URI("http://localhost:80/%s".formatted(workflowName)));
        final ByteArrayInputStream requestStream = new ByteArrayInputStream(payload.getBytes(StandardCharsets.UTF_8));
        Mockito.when(httpExchange.getRequestBody()).thenReturn(requestStream);
        final ByteArrayOutputStream responseStream = new ByteArrayOutputStream();
        Mockito.when(httpExchange.getResponseBody()).thenReturn(responseStream);


        syncHandler.handle(httpExchange);


        Assertions.assertThat(responseStream.toString(StandardCharsets.UTF_8)).isEqualTo(payload);
        Mockito.verify(httpExchange).sendResponseHeaders(Mockito.eq(200), Mockito.anyLong());
    }

    @Example
    void getWorkflowFromUriIllegalArgumentException() throws URISyntaxException {
        final WorkflowFactory workflowFactory = Mockito.mock(WorkflowFactory.class);
        final SyncHandler syncHandler = new SyncHandler(workflowFactory);

        final URI uri = new URI("http://localhost:80");
        Assertions
                .assertThatCode(() -> syncHandler.getWorkflowFromUri(uri))
                .isInstanceOf(IllegalArgumentException.class);
    }
}