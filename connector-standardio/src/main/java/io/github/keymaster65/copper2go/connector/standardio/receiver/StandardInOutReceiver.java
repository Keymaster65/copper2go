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
import io.github.keymaster65.copper2go.connector.standardio.reply.StandardInOutReplyChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;

public class StandardInOutReceiver {

    private static final Logger log = LoggerFactory.getLogger(StandardInOutReceiver.class);
    private final BufferedReader reader;

    public StandardInOutReceiver(final BufferedReader reader) {
        this.reader = reader;
    }

    public void listenLocalStream(final PayloadReceiver payloadReceiver) throws StandardInOutException {
        //noinspection InfiniteLoopStatement
        while (true) {
            try {
                System.out.println("Enter your name: "); // NOSONAR
                String line1 = reader.readLine();
                log.debug("line: {}", line1);
                if (line1 == null) {
                    throw new NullPointerException("Read a 'null' line. So there seems to be no stdin. Might happen when starting with gradle.");
                }
                if ("exit".equals(line1)) {
                    throw new StandardInOutException("Input canceled by 'exit' line.");
                }
                payloadReceiver.receive(
                        line1,
                        new StandardInOutReplyChannel(System.out, System.err), // NOSONAR
                        "Hello",
                        1,
                        0)
                ;
            } catch (Exception e) {
                throw new StandardInOutException("Exception while getting input.", e);
            }
        }
    }
}
