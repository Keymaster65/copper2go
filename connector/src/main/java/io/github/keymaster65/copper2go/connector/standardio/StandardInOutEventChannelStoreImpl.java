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
package io.github.keymaster65.copper2go.connector.standardio;

import io.github.keymaster65.copper2go.connectorapi.EventChannel;
import io.github.keymaster65.copper2go.api.workflow.EventChannelStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class StandardInOutEventChannelStoreImpl implements EventChannelStore {
    private static final Logger log = LoggerFactory.getLogger(StandardInOutEventChannelStoreImpl.class);

    private Map<String, EventChannel> eventChannelMap = new ConcurrentHashMap<>();

    public StandardInOutEventChannelStoreImpl() {
        eventChannelMap.put("System.stdout", new StandardOutEventChannelImpl());
    }

    @Override
    public void event(
            final String channelName,
            final String event,
            final Map<String, String> attributes
    ) {
        if (attributes != null) {
            log.warn("Ignore attributes {}", attributes);
        }
        eventChannelMap.get(channelName).event(event);
    }

    @Override
    public void errorEvent(
            final String channelName,
            final String event,
            final Map<String, String> attributes
    ) {
        if (attributes != null) {
            log.warn("Ignore attributes {}", attributes);
        }
        eventChannelMap.get(channelName).errorEvent(event);
    }
}
