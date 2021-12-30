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

import net.jqwik.api.Example;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class DefaultRequestChannelStoreTest {

    public static final String NAME = "NAME";
    public static final String REQUEST = "request";
    public static final String RESPONSE_CORRELATION_ID = "responseCorrelationId";

    @Test
    void requestNotExisting() {
        final DefaultRequestChannelStore defaultRequestChannelStore = new DefaultRequestChannelStore();

        Assertions.assertThatCode(() ->
                        defaultRequestChannelStore.request(NAME, REQUEST, RESPONSE_CORRELATION_ID)
                ).isInstanceOf(NullPointerException.class)
                .hasMessage("Channel with name NAME must not be null.");
    }

    @Test
    void requestAfterPutExisting() {
        final DefaultRequestChannelStore defaultRequestChannelStore = new DefaultRequestChannelStore();
        final RequestChannel requestChannel = Mockito.mock(RequestChannel.class);

        defaultRequestChannelStore.put(NAME, requestChannel);
        defaultRequestChannelStore.request(NAME, REQUEST, RESPONSE_CORRELATION_ID);

        Mockito.verify(requestChannel).request(REQUEST, null, RESPONSE_CORRELATION_ID);
    }


    @Test
    void putTwice() {
        final DefaultRequestChannelStore defaultRequestChannelStore = new DefaultRequestChannelStore();
        final RequestChannel requestChannel = Mockito.mock(RequestChannel.class);

        defaultRequestChannelStore.put(NAME, requestChannel);

        Assertions.assertThatCode(() ->
                        defaultRequestChannelStore.put(NAME, requestChannel)
                ).isInstanceOf(EngineRuntimeException.class)
                .hasMessageStartingWith("Duplicate RequestChannel");
    }

    @Test
    void close() {
        final DefaultRequestChannelStore defaultRequestChannelStore = new DefaultRequestChannelStore();
        final RequestChannel requestChannel = Mockito.mock(RequestChannel.class);
        defaultRequestChannelStore.put(NAME, requestChannel);

        defaultRequestChannelStore.close();

        Mockito.verify(requestChannel).close();
    }

    @Test
    void closeEmpty() {
        final DefaultRequestChannelStore defaultRequestChannelStore = new DefaultRequestChannelStore();

        Assertions.assertThatCode(defaultRequestChannelStore::close).doesNotThrowAnyException();
    }

    static class EngineExceptionTest {

        public static final String MESSAGE = "message";
        public static final String MESSAGE2 = "message2";

        @Example
        void constructorMessage() {
            final EngineException engineException = new EngineException(MESSAGE);

            Assertions.assertThat(engineException.getMessage()).isEqualTo(MESSAGE);
        }

        @Example
        void constructorMessageCause() {
            final EngineException cause = new EngineException(MESSAGE2);
            final EngineException engineException = new EngineException(MESSAGE, cause);

            Assertions.assertThat(engineException.getMessage()).isEqualTo(MESSAGE);
            Assertions.assertThat(engineException.getCause()).isSameAs(cause);
        }
    }

    static class EngineRuntimeExceptionTest {

        public static final String MESSAGE = "message";
        public static final String MESSAGE2 = "message2";

        @Example
        void constructorMessage() {
            final EngineRuntimeException engineRuntimeException = new EngineRuntimeException(MESSAGE);

            Assertions.assertThat(engineRuntimeException.getMessage()).isEqualTo(MESSAGE);
        }

        @Example
        void constructorMessageCause() {
            final EngineException cause = new EngineException(MESSAGE2);
            final EngineRuntimeException engineRuntimeException = new EngineRuntimeException(MESSAGE, cause);

            Assertions.assertThat(engineRuntimeException.getMessage()).isEqualTo(MESSAGE);
            Assertions.assertThat(engineRuntimeException.getCause()).isSameAs(cause);
        }
    }

}