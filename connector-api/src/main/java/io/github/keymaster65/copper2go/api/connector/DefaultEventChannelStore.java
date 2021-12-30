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
package io.github.keymaster65.copper2go.api.connector;

import io.github.keymaster65.copper2go.api.workflow.EventChannelStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class DefaultEventChannelStore implements EventChannelStore {
    private static final Logger log = LoggerFactory.getLogger(DefaultEventChannelStore.class);

    private Map<String, EventChannel> eventChannelMap = new ConcurrentHashMap<>();

    public void put(final String name, final EventChannel eventChannel) {
        final EventChannel oldEventChannel = eventChannelMap.putIfAbsent(name, eventChannel);
        if (oldEventChannel != null) {
            throw new EngineRuntimeException(String.format("Duplicate EventChannel %s found: %s", name, oldEventChannel));
        }
    }

    @Override
    public void event(
            final String channelName,
            final String event,
            final Map<String, String> attributes
    ) {
        final EventChannel eventChannel = eventChannelMap.get(channelName);
        Objects.requireNonNull(eventChannel, String.format("EventChannel with name %s must not be null.", channelName));

        if (attributes != null) {
            log.warn("Ignore attributes {}", attributes);
        }
        eventChannel.event(event);
    }

    @Override
    public void errorEvent(
            final String channelName,
            final String event,
            final Map<String, String> attributes
    ) {
        final EventChannel eventChannel = eventChannelMap.get(channelName);
        Objects.requireNonNull(eventChannel, String.format("EventChannel with name %s must not be null.", channelName));

        if (attributes != null) {
            log.warn("Ignore attributes {}", attributes);
        }
        eventChannel.errorEvent(event);
    }
}
