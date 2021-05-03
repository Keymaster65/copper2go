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
package io.github.keymaster65.copper2go.util;

import io.github.keymaster65.copper2go.workflowapi.ReplyChannelStore;
import io.github.keymaster65.copper2go.workflowapi.WorkflowData;
import org.copperengine.core.CopperException;
import org.copperengine.core.tranzient.TransientScottyEngine;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class WorkflowTestRunnerTest {

    private static final String WORKFLOW_DIR = "./src/test/java";
    private static final String UUID = "UUID";
    private static final String TEST_DATA = "WolfX";
    private static final String WORKFLOW_NAME = "EchoTestWorkflow";

    @Test
    void runTest() throws CopperException {
        final ReplyChannelStore replyChannelStoreMock = mock(ReplyChannelStore.class);

        TransientScottyEngine engine = WorkflowTestRunner.createTestEngine(
                WORKFLOW_DIR,
                new Copper2goDependencyInjector(
                        replyChannelStoreMock,
                        null,
                        null
                )
        );
        WorkflowTestRunner.runTest(
                new WorkflowData(UUID, TEST_DATA),
                new WorkflowTestRunner.WorkflowDefinition(WORKFLOW_NAME, 1L, 0L),
                engine
        );

        verify(replyChannelStoreMock).reply(UUID, TEST_DATA);
    }
}