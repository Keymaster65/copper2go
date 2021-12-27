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
package io.github.keymaster65.copper2go.workflowapi;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Map;

class EventChannelStoreTest {

    public static final String CHANNEL_NAME = "channelName";
    public static final String EVENT = "event";

    @Test
    void event() {
        final EventChannelStore mockedEventChannelStore = Mockito.mock(EventChannelStore.class);
        final EventChannelStore eventChannelStore = createEventChannelStore(mockedEventChannelStore);

        eventChannelStore.event(CHANNEL_NAME, EVENT);

        Mockito.verify(mockedEventChannelStore).event(CHANNEL_NAME, EVENT, null);
    }

    @Test
    void errorEvent() {
        final EventChannelStore mockedEventChannelStore = Mockito.mock(EventChannelStore.class);
        final EventChannelStore eventChannelStore = createEventChannelStore(mockedEventChannelStore);

        eventChannelStore.errorEvent(CHANNEL_NAME, EVENT);

        Mockito.verify(mockedEventChannelStore).errorEvent(CHANNEL_NAME, EVENT, null);
    }

    private EventChannelStore createEventChannelStore(final EventChannelStore wrappedEventChannelStore) {
        return new EventChannelStore() {

            @Override
            public void event(final String channelName, final String event, final Map<String, String> attributes) {
                wrappedEventChannelStore.event(channelName, event, attributes);
            }

            @Override
            public void errorEvent(final String channelName, final String event, final Map<String, String> attributes) {
                wrappedEventChannelStore.errorEvent(channelName, event, attributes);
            }
        };
    }
}