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
package io.github.keymaster65.copper2go.api.workflow;

import java.util.Map;

/**
 * Store for defined request channels, that can be used by name in workflows to submit requests.
 */
public interface RequestChannelStore {

    /**
     * Submit request without attributes.
     *
     * @param channelName defined channel name
     * @param request request payload
     * @param responseCorrelationId identifies the correlation for a request/response pair
     */
    default void request(
            final String channelName,
            final String request,
            final String responseCorrelationId
    ) {
        request(channelName, request, null, responseCorrelationId);
    }

    /**
     /**
     * Submit request with attributes.
     *
     * @param channelName defined channel name
     * @param request request payload
     * @param attributes additional attributes
     * @param responseCorrelationId identifies the correlation for a request/response pair
     */
    void request(
            final String channelName,
            final String request,
            Map<String,String> attributes,
            final String responseCorrelationId
    );
}