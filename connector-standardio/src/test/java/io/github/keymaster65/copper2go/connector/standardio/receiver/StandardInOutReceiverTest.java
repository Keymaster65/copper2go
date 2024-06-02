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
package io.github.keymaster65.copper2go.connector.standardio.receiver;

import io.github.keymaster65.copper2go.api.connector.PayloadReceiver;
import io.github.keymaster65.copper2go.connector.standardio.StandardInOutException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.BufferedReader;
import java.io.IOException;

class StandardInOutReceiverTest {

    @Test
    void listenLocalStream() throws IOException {
        final BufferedReader bufferedReader = Mockito.mock(BufferedReader.class);
        final PayloadReceiver payloadReceiver = Mockito.mock(PayloadReceiver.class);
        final StandardInOutReceiver standardInOutReceiver = new StandardInOutReceiver(bufferedReader);
        Mockito.when(bufferedReader.readLine())
                .thenReturn("line1")
                .thenReturn("exit");

        Assertions.assertThatCode(() ->
                        standardInOutReceiver.listenLocalStream(payloadReceiver)
                )
                .isInstanceOf(StandardInOutException.class)
                .hasMessage("Exception while getting input.")
                .hasRootCauseInstanceOf(StandardInOutException.class)
                .hasRootCauseMessage("Input canceled by 'exit' line.");
    }

    @Test
    void listenLocalStreamNullLine() {
        final BufferedReader bufferedReader = Mockito.mock(BufferedReader.class);
        final PayloadReceiver payloadReceiver = Mockito.mock(PayloadReceiver.class);
        final StandardInOutReceiver standardInOutReceiver = new StandardInOutReceiver(bufferedReader);

        Assertions.assertThatCode(() ->
                        standardInOutReceiver.listenLocalStream(payloadReceiver)
                )
                .isInstanceOf(StandardInOutException.class)
                .hasMessage("Exception while getting input.")
                .hasRootCauseInstanceOf(NullPointerException.class)
                .hasRootCauseMessage("Read a 'null' line. So there seems to be no stdin. Might happen when starting with gradle.");

    }
}