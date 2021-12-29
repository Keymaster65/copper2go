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
package io.github.keymaster65.copper2go.engine;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class PayloadReceiverTest {

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