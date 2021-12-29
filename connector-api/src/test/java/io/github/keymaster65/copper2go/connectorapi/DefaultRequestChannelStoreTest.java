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

import net.jqwik.api.Example;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;

import java.util.Map;

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

    static class EventChannelTest {

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

    static class PayloadReceiverTest {

        public static final String PAYLOAD = "payload";
        public static final long MINOR = 0;
        public static final long MAJOR = 0;
        public static final String WORKFLOW = "workflow";
        public static final ReplyChannel REPLY_CHANNEL = Mockito.mock(ReplyChannel.class);

        @Test
        void receive() throws EngineException {
            final PayloadReceiver mockedPayloadReceiver = Mockito.mock(PayloadReceiver.class);
            final PayloadReceiver PayloadReceiver = createPayloadReceiver(mockedPayloadReceiver);

            PayloadReceiver.receive(
                    PAYLOAD,
                    REPLY_CHANNEL,
                    WORKFLOW,
                    MAJOR,
                    MINOR
            );

            Mockito.verify(mockedPayloadReceiver).receive(
                    PAYLOAD,
                    null,
                    REPLY_CHANNEL,
                    WORKFLOW,
                    MAJOR,
                    MINOR
            );
        }

        private PayloadReceiver createPayloadReceiver(final PayloadReceiver wrappedPayloadReceiver) {
            //noinspection FunctionalExpressionCanBeFolded
            return wrappedPayloadReceiver::receive;
        }
    }

    static class ReplyChannelTest {
        public static final String MESSAGE = "message";


        @Test
        void reply() {
            final ReplyChannel mockedReplyChannel = Mockito.mock(ReplyChannel.class);
            final ReplyChannel ReplyChannel = createReplyChannel(mockedReplyChannel);

            ReplyChannel.reply(MESSAGE);

            Mockito.verify(mockedReplyChannel).reply(MESSAGE, null);
        }

        @Test
        void replyError() {
            final ReplyChannel mockedReplyChannel = Mockito.mock(ReplyChannel.class);
            final ReplyChannel ReplyChannel = createReplyChannel(mockedReplyChannel);

            ReplyChannel.replyError(MESSAGE);

            Mockito.verify(mockedReplyChannel).replyError(MESSAGE, null);
        }

        private ReplyChannel createReplyChannel(final ReplyChannel wrappedReplyChannel) {
            return new ReplyChannel() {

                @Override
                public void reply(final String reply, final Map<String, String> attributes) {
                    wrappedReplyChannel.reply(reply, attributes);
                }

                @Override
                public void replyError(final String reply, final Map<String, String> attributes) {
                    wrappedReplyChannel.replyError(reply, attributes);
                }
            };
        }
    }

    static class RequestChannelTest {

        public static final String REQUEST = "request";
        public static final String RESPONSE_CORRELATION_ID = "responseCorrelationId";

        @Test
        void request() {
            final RequestChannel mockedRequestChannel = Mockito.mock(RequestChannel.class);
            final RequestChannel requestChannel = createRequestChannel(mockedRequestChannel);

            requestChannel.request(REQUEST, RESPONSE_CORRELATION_ID);

            Mockito.verify(mockedRequestChannel).request(REQUEST, null, RESPONSE_CORRELATION_ID);

        }

        private RequestChannel createRequestChannel(final RequestChannel wrappedRequestChannel) {
            return new RequestChannel() {

                @Override
                public void request(final String request, final Map<String, String> attributes, final String responseCorrelationId) {
                    wrappedRequestChannel.request(request, attributes, responseCorrelationId);
                }

                @Override
                public void close() {
                    wrappedRequestChannel.close();
                }
            };
        }
    }

    static class WorkflowVersionTest {

        @ParameterizedTest
        @ValueSource(strings = {
                "/1.2/Hello",
                "localhost/1.2/Hello",
                "http://localhost/1.2/Hello",
                "http://localhost:80/1.2/Hello",
                "http://localhost:80/demoapp/1.2/Hello",
                "/1.2/Hello?",
                "/1.2/Hello?a=1",
                "/1.2/Hello?a=1&b=2"
        })
        void getWorkflow(final String uri) throws EngineException {
            WorkflowVersion workflowVersion = WorkflowVersion.of(uri);
            Assertions.assertThat(workflowVersion.name).isEqualTo("Hello");
            Assertions.assertThat(workflowVersion.major).isEqualTo(1L);
            Assertions.assertThat(workflowVersion.minor).isEqualTo(2L);
        }

        @ParameterizedTest
        @ValueSource(strings = {
                "",
                "a/w",
                "v1.1/w",
                "1/w",
                "w"
        })
        void badUri(final String uri) {
            Assertions.assertThatExceptionOfType(EngineException.class).isThrownBy(() -> WorkflowVersion.of(uri));
        }
    }
}