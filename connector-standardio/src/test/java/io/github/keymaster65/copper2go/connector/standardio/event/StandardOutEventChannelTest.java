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
package io.github.keymaster65.copper2go.connector.standardio.event;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.PrintStream;
import java.util.Map;

class StandardOutEventChannelTest {

    public static final String MESSAGE = "message";

    @Test
    void event() {
        final PrintStream printStream = Mockito.mock(PrintStream.class);
        final PrintStream errorPrintStream = Mockito.mock(PrintStream.class);
        final StandardOutEventChannel standardOutEventChannel = new StandardOutEventChannel(printStream, errorPrintStream);

        standardOutEventChannel.event(MESSAGE, Map.of());
        standardOutEventChannel.event(MESSAGE, null);

        Mockito.verify(printStream, Mockito.times(2)).println(MESSAGE);
    }

    @Test
    void errorEvent() {
        final PrintStream printStream = Mockito.mock(PrintStream.class);
        final PrintStream errorPrintStream = Mockito.mock(PrintStream.class);
        final StandardOutEventChannel standardOutEventChannel = new StandardOutEventChannel(printStream, errorPrintStream);

        standardOutEventChannel.errorEvent(MESSAGE, Map.of());
        standardOutEventChannel.errorEvent(MESSAGE, null);

        Mockito.verify(errorPrintStream, Mockito.times(2)).println(MESSAGE);
    }
}