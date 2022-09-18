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
package io.github.keymaster65.copper2go.engine.vanilla.impl;

import net.jqwik.api.Example;
import org.mockito.Mockito;

import java.util.function.Consumer;

class EarlyResponseRunnableFactoryTest {
    public static final String RESPONSE = "response";
    public static final String CORRELATION_ID = "correlationId";

    @Example
    void createEarlyResponseRunnableForResponse() {
        @SuppressWarnings("unchecked") final Consumer<String> consumer = Mockito.mock(Consumer.class);
        EarlyResponseRunnableFactory.createEarlyResponseRunnable(
                CORRELATION_ID,
                consumer,
                new Continuation(RESPONSE)
        ).run();

        Mockito.verify(consumer).accept(RESPONSE);
    }

    @Example
    void createEarlyResponseRunnableForContinuation() {
        @SuppressWarnings("unchecked") final Consumer<String> waitingConsumer = Mockito.mock(Consumer.class);
        EarlyResponseRunnableFactory.createEarlyResponseRunnable(
                CORRELATION_ID,
                RESPONSE,
                new Continuation(waitingConsumer)
        ).run();

        Mockito.verify(waitingConsumer).accept(RESPONSE);
    }
}