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
package io.github.keymaster65.copper2go.connectorapi;

import io.github.keymaster65.copper2go.workflowapi.RequestChannelStore;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class DefaultRequestChannelStore implements RequestChannelStore {

    private final Map<String, RequestChannel> requestChannelMap = new ConcurrentHashMap<>();

    public void put(final String name, final RequestChannel requestChannel) {
        final RequestChannel oldRequestChannel = requestChannelMap.putIfAbsent(name, requestChannel);
        if (oldRequestChannel != null) {
            throw new EngineRuntimeException(String.format("Duplicate RequestChannel %s found: %s", name, oldRequestChannel));
        }
    }

    @Override
    public void request(
            final String channelName,
            final String request,
            Map<String, String> attributes,
            final String responseCorrelationId
    ) {
        Objects.requireNonNull(requestChannelMap.get(channelName), String.format("Channel with name %s must not be null.", channelName));
        requestChannelMap.get(channelName).request(request, attributes, responseCorrelationId);
    }

    public void close() {
        requestChannelMap.values().forEach(RequestChannel::close);
    }
}
