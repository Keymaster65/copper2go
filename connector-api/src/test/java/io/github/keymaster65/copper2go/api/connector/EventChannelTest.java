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

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Map;

class EventChannelTest {

    public static final String MESSAGE = "message";

    @Test
    void event() {
        final EventChannel mockedEventChannel = Mockito.mock(EventChannel.class);
        final EventChannel eventChannel = createEventChannel(mockedEventChannel);

        eventChannel.event(MESSAGE);

        Mockito.verify(mockedEventChannel).event(MESSAGE, null);
    }

    @Test
    void errorEvent() {
        final EventChannel mockedEventChannel = Mockito.mock(EventChannel.class);
        final EventChannel eventChannel = createEventChannel(mockedEventChannel);

        eventChannel.errorEvent(MESSAGE);

        Mockito.verify(mockedEventChannel).errorEvent(MESSAGE, null);
    }

    private EventChannel createEventChannel(final EventChannel wrappedEventChannel) {
        return new EventChannel() {

            @Override
            public void event(final String message, final Map<String, String> attributes) {
                wrappedEventChannel.event(message, attributes);
            }

            @Override
            public void errorEvent(final String message, final Map<String, String> attributes) {
                wrappedEventChannel.errorEvent(message, attributes);
            }
        };
    }
}
