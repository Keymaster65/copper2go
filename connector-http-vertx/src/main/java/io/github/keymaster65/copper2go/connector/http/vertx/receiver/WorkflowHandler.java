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
import io.github.keymaster65.copper2go.api.connector.WorkflowVersion;
import io.github.keymaster65.copper2go.connector.http.vertx.reply.HttpReplyChannel;
import io.vertx.core.http.HttpServerResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.HttpURLConnection;
import java.util.Map;

public class WorkflowHandler {

    private WorkflowHandler() {}

    private static final Logger log = LoggerFactory.getLogger(WorkflowHandler.class);

    static void handleWorkflow(
            final String requestBody,
            final HttpServerResponse response,
            final Map<String, String> attributes,
            final String uri,
            final PayloadReceiver payloadReceiver
    ) {
        try {
            if (ApiPath.isPayloadUri(uri)) {
                var workflowVersion = WorkflowVersion.of(uri);
                log.debug("Call Workflow on engine {}.", payloadReceiver);
                payloadReceiver.receive(
                        requestBody,
                        attributes,
                        new HttpReplyChannel(response),
                        workflowVersion.name,
                        workflowVersion.major,
                        workflowVersion.minor
                );
            } else {
                throw new IllegalArgumentException(String.format("PATH %s not as expected.", uri));
            }

            if (ApiPath.isOnewayUri(uri)) {
                log.debug("Empty OK response for incoming event.");
                response
                        .setStatusCode(HttpURLConnection.HTTP_ACCEPTED)
                        .end();
            }
        } catch (EngineException | RuntimeException e) {
            log.warn("Exception while calling workflow.", e);
            response
                    .setStatusCode(HttpURLConnection.HTTP_INTERNAL_ERROR)
                    .end(String.format("Exception: %s", e.getMessage()));
        }
    }
}
