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

import io.github.keymaster65.copper2go.api.connector.EventChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.PrintStream;
import java.util.Map;

public class StandardOutEventChannel implements EventChannel {
    private static final Logger log = LoggerFactory.getLogger(StandardOutEventChannel.class);
    private final PrintStream printStream;
    private final PrintStream errorPrintStream;

    public StandardOutEventChannel(
            final PrintStream printStream,
            final PrintStream errorPrintStream
    ) {

        this.printStream = printStream;
        this.errorPrintStream = errorPrintStream;
    }
    @Override
    public void event(final String event, final Map<String, String> attributes) {
        if (attributes != null) {
            log.warn("Ignore attributes {}", attributes);
        }
        printStream.println(event); // NOSONAR
    }

    @Override
    public void errorEvent(final String event, final Map<String, String> attributes) {
        if (attributes != null) {
            log.warn("Ignore attributes {}", attributes);
        }
        errorPrintStream.println(event); // NOSONAR
    }
}
