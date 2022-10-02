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

import com.sun.net.httpserver.HttpExchange; // NOSONAR
import com.sun.net.httpserver.HttpHandler;
import io.github.keymaster65.copper2go.engine.sync.workflowapi.Workflow;
import io.github.keymaster65.copper2go.engine.sync.workflowapi.WorkflowData;
import io.github.keymaster65.copper2go.engine.sync.workflowapi.WorkflowFactory;
import io.github.keymaster65.copper2go.sync.application.workflow.WorkflowDataImpl;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;

public class SyncHandler implements HttpHandler {
    private final WorkflowFactory workflowFactory;

    public SyncHandler(final WorkflowFactory workflowFactory) {
        this.workflowFactory = workflowFactory;
    }

    @Override
    public void handle(final HttpExchange exchange) throws IOException {
        final byte[] payloadBytes = exchange.getRequestBody().readAllBytes();
        final Workflow workflow = getWorkflowFromUri(exchange.getRequestURI());
        final WorkflowData workflowData = new WorkflowDataImpl(new String(payloadBytes, StandardCharsets.UTF_8));

        final String response = workflow.main(workflowData);

        try (OutputStream responseBody = exchange.getResponseBody()) {
            final byte[] reponseBytes = response.getBytes(StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(200, reponseBytes.length);
            responseBody.write(reponseBytes);
        }
    }

    Workflow getWorkflowFromUri(final URI uri) {
        if (uri.toString().contains("Hello")) {
            return workflowFactory.create("Hello", 2, 0);
        } else if (uri.toString().contains("Pricing")) {
            return workflowFactory.create("Pricing", 1, 0);
        }
        throw new IllegalArgumentException("Can't get workflow for %s.".formatted(uri));
    }
}
