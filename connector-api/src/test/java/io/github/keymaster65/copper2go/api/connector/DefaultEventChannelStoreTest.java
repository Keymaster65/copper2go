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

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


class DefaultEventChannelStoreTest {

    public static final String NAME = "NAME";
    public static final String EVENT = "event";

    @Test
    void eventNotExisting() {
        final DefaultEventChannelStore defaultRequestChannelStore = new DefaultEventChannelStore();

        Assertions.assertThatCode(() ->
                        defaultRequestChannelStore.event(NAME, EVENT)
                ).isInstanceOf(NullPointerException.class)
                .hasMessage("EventChannel with name NAME must not be null.");
    }

    @Test
    void errorEventNotExisting() {
        final DefaultEventChannelStore defaultRequestChannelStore = new DefaultEventChannelStore();

        Assertions.assertThatCode(() ->
                        defaultRequestChannelStore.errorEvent(NAME, EVENT)
                ).isInstanceOf(NullPointerException.class)
                .hasMessage("EventChannel with name NAME must not be null.");
    }

    @Test
    void eventAfterPutExisting() {
        final DefaultEventChannelStore defaultEventChannelStore = new DefaultEventChannelStore();
        final EventChannel eventChannel = Mockito.mock(EventChannel.class);

        defaultEventChannelStore.put(NAME, eventChannel);
        defaultEventChannelStore.event(NAME, EVENT);

        Mockito.verify(eventChannel).event(EVENT);
    }

    @Test
    void errorEventAfterPutExisting() {
        final DefaultEventChannelStore defaultEventChannelStore = new DefaultEventChannelStore();
        final EventChannel eventChannel = Mockito.mock(EventChannel.class);

        defaultEventChannelStore.put(NAME, eventChannel);
        defaultEventChannelStore.errorEvent(NAME, EVENT);

        Mockito.verify(eventChannel).errorEvent(EVENT);
    }

    @Test
    void putTwice() {
        final DefaultEventChannelStore defaultEventChannelStore = new DefaultEventChannelStore();
        final EventChannel eventChannel = Mockito.mock(EventChannel.class);

        defaultEventChannelStore.put(NAME, eventChannel);

        Assertions.assertThatCode(() ->
                defaultEventChannelStore.put(NAME, eventChannel)
                ).isInstanceOf(EngineRuntimeException.class)
                .hasMessageStartingWith("Duplicate EventChannel");
    }
}