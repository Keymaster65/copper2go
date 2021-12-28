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

import io.github.keymaster65.copper2go.engine.EngineException;
import io.github.keymaster65.copper2go.engine.ReplyChannel;
import org.assertj.core.api.Assertions;
import org.copperengine.core.CopperException;
import org.copperengine.core.common.WorkflowRepository;
import org.copperengine.core.tranzient.TransientScottyEngine;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class PayloadReceiverImplTest {

    @Test
    void receive() throws EngineException, CopperException {
        final TransientScottyEngine scottyEngine = Mockito.mock(TransientScottyEngine.class);
        final PayloadReceiverImpl payloadReceiver = new PayloadReceiverImpl(
                scottyEngine,
                Mockito.mock(ReplyChannelStoreImpl.class)
        );
        final WorkflowRepository workflowRepository = Mockito.mock(WorkflowRepository.class);
        Mockito.when(scottyEngine.getWfRepository()).thenReturn(workflowRepository);
        // needs final mocking enabled
        Mockito.when(scottyEngine.createUUID()).thenReturn("uuid");

        payloadReceiver.receive(
                "payload",
                null,
                Mockito.mock(ReplyChannel.class)
                , "Ignore"
                , 1
                , 0

        );

        Mockito.verify(scottyEngine).run(Mockito.any());
    }

    @Test
    void receiveRunException() throws CopperException {
        final TransientScottyEngine scottyEngine = Mockito.mock(TransientScottyEngine.class);
        final PayloadReceiverImpl payloadReceiver = new PayloadReceiverImpl(
                scottyEngine,
                Mockito.mock(ReplyChannelStoreImpl.class)
        );
        final WorkflowRepository workflowRepository = Mockito.mock(WorkflowRepository.class);
        Mockito.when(scottyEngine.getWfRepository()).thenReturn(workflowRepository);
        // needs final mocking enabled
        Mockito.when(scottyEngine.createUUID()).thenReturn("uuid");
        Mockito.when(scottyEngine.run(Mockito.any())).thenThrow(new CopperException("Test only"));

        Assertions.assertThatCode(() ->
                        payloadReceiver.receive(
                                "payload",
                                null,
                                Mockito.mock(ReplyChannel.class)
                                , "Ignore"
                                , 1
                                , 0

                        ))
                .isInstanceOf(EngineException.class)
                .hasMessage("Exception while running workflow.");
    }
}