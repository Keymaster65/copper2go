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
package io.github.keymaster65.copper2go.connector.standardio.reply;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.PrintStream;
import java.util.Map;


class StandardInOutReplyChannelTest {

    public static final String MESSAGE = "message";

    @Test
    void reply() {
        final PrintStream printStream = Mockito.mock(PrintStream.class);
        final PrintStream errorPrintStream = Mockito.mock(PrintStream.class);
        final StandardInOutReplyChannel standardInOutReplyChannel = new StandardInOutReplyChannel(printStream, errorPrintStream);

        standardInOutReplyChannel.reply(MESSAGE, Map.of());
        standardInOutReplyChannel.reply(MESSAGE, null);

        Mockito.verify(printStream, Mockito.times(2)).println(MESSAGE);
    }

    @Test
    void replyError() {
        final PrintStream printStream = Mockito.mock(PrintStream.class);
        final PrintStream errorPrintStream = Mockito.mock(PrintStream.class);
        final StandardInOutReplyChannel standardInOutReplyChannelImp = new StandardInOutReplyChannel(printStream, errorPrintStream);

        standardInOutReplyChannelImp.replyError(MESSAGE, Map.of());
        standardInOutReplyChannelImp.replyError(MESSAGE, null);

        Mockito.verify(errorPrintStream, Mockito.times(2)).println(MESSAGE);
    }
}