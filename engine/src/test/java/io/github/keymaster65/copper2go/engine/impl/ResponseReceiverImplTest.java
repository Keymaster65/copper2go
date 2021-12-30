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
package io.github.keymaster65.copper2go.engine.impl;

import io.github.keymaster65.copper2go.api.connector.ResponseReceiver;
import org.assertj.core.api.Assertions;
import org.copperengine.core.tranzient.TransientScottyEngine;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class ResponseReceiverImplTest {

    @Test
    void receiveResponseEngine() {
        final TransientScottyEngine scottyEngine = Mockito.mock(TransientScottyEngine.class);
        ResponseReceiver responseReceiver = new ResponseReceiverImpl(scottyEngine);

        Assertions
                .assertThatNoException()
                .isThrownBy(() -> responseReceiver.receive("responseCorrelationId", "response"));
        Mockito.verify(scottyEngine).notify(Mockito.any(), Mockito.any());
    }

    @Test
    void receiveErrorResponseEngine() {
        final TransientScottyEngine scottyEngine = Mockito.mock(TransientScottyEngine.class);
        ResponseReceiver responseReceiver = new ResponseReceiverImpl(scottyEngine);

        Assertions
                .assertThatNoException()
                .isThrownBy(() -> responseReceiver.receiveError("responseCorrelationId", "response"));
        Mockito.verify(scottyEngine).notify(Mockito.any(), Mockito.any());
    }
}
