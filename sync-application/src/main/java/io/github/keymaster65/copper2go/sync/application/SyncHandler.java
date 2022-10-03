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

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.stream.Collectors;

public class SyncHandler implements HttpHandler {
    private final WorkflowFactory workflowFactory;
    public static final String LICENSE_INDEX_HTML = "license/index.html";
    public static final String LICENSE_PATH = "license";

    public SyncHandler(final WorkflowFactory workflowFactory) {
        this.workflowFactory = workflowFactory;
    }

    @Override
    public void handle(final HttpExchange exchange) throws IOException {
        try {
            final Workflow workflow = getWorkflowFromUri(exchange.getRequestURI());
            handleWorkflow(workflow, exchange);
        } catch (UnknownWorkflowException unknownWorkflowException) {
            handleLicense(exchange);
        }
    }

    void handleWorkflow(final Workflow workflow, final HttpExchange exchange) throws IOException {
        final byte[] payloadBytes = exchange.getRequestBody().readAllBytes();

        final WorkflowData workflowData = new WorkflowDataImpl(new String(payloadBytes, StandardCharsets.UTF_8));

        final String response = workflow.main(workflowData);

        sendResponse(exchange, response);
    }

    void handleLicense(final HttpExchange exchange) throws IOException {
        final String uri = exchange.getRequestURI().getPath();
        String path;
        if ("/".equals(uri) || "/.".equals(uri)) {
            path = LICENSE_INDEX_HTML;
        } else {
            path = LICENSE_PATH + uri;
        }



        try (
                final InputStream inputStream
                        = Objects.requireNonNull(ClassLoader.getSystemClassLoader().getResourceAsStream(path))
        ) {
            final boolean allowed = new File(path).getCanonicalPath().startsWith(new File(LICENSE_PATH).getCanonicalPath());
            if (!allowed) {
                throw new IllegalArgumentException("Bad path.");
            }
            String resource
                    = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))
                    .lines()
                    .collect(Collectors.joining("\n"));
            sendResponse(exchange, resource);

        } catch (Exception e) {
            sendResponse(exchange, e);
        }
    }

    private static void sendResponse(final HttpExchange exchange, final String response) throws IOException {
        try (OutputStream responseBody = exchange.getResponseBody()) {
            final byte[] reponseBytes = response.getBytes(StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(200, reponseBytes.length);
            responseBody.write(reponseBytes);
        }
    }

    private static void sendResponse(final HttpExchange exchange, final Exception exception) throws IOException {
        try (OutputStream responseBody = exchange.getResponseBody()) {
            String message = exception.getMessage();
            if (message == null) {
                message = "Not found.";
            }
            final byte[] reponseBytes = message.getBytes(StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(404, reponseBytes.length);
            responseBody.write(reponseBytes);
        }
    }

    Workflow getWorkflowFromUri(final URI uri) throws UnknownWorkflowException {
        if (uri.toString().contains("Hello")) {
            return workflowFactory.create("Hello", 2, 0);
        } else if (uri.toString().contains("Pricing")) {
            return workflowFactory.create("Pricing", 1, 0);
        }
        throw new UnknownWorkflowException("Can't get workflow for %s.".formatted(uri));
    }
}
