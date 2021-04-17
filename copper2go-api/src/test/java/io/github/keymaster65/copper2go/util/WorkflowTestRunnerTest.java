package io.github.keymaster65.copper2go.util;

import io.github.keymaster65.copper2go.workflowapi.ReplyChannelStore;
import io.github.keymaster65.copper2go.workflowapi.WorkflowData;
import org.copperengine.core.CopperException;
import org.copperengine.core.tranzient.TransientScottyEngine;
import org.junit.jupiter.api.Test;

import static org.mockito.ArgumentMatchers.eq;
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