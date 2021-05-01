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

import io.github.keymaster65.copper2go.engine.Copper2GoEngine;
import io.github.keymaster65.copper2go.engine.EngineException;
import io.github.keymaster65.copper2go.engine.WorkflowRepositoryConfig;
import io.github.keymaster65.copper2go.util.Copper2goDependencyInjector;
import org.assertj.core.api.Assertions;
import org.copperengine.core.DependencyInjector;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class Copper2GoEngineImplTest {

    private static Copper2GoEngine copper2GoEngine;
    private static ReplyChannelStoreImpl replyChannelStoreImpl = new ReplyChannelStoreImpl();
    private final DependencyInjector dependencyInjector = new Copper2goDependencyInjector(
            replyChannelStoreImpl,
            null,
            null
    );

    @BeforeAll
    static void createEngine() {
        copper2GoEngine = createCopper2GoEngine();
    }

    @Test
    void callWorkflowEngineNotStarted() {
        Assertions
                .assertThatExceptionOfType(NullPointerException.class)
                .isThrownBy(() -> copper2GoEngine.callWorkflow("", null, "Hello", 1L, 0L))
                .withMessage("No engine found. May be it must be started first.");
    }

    @Test
    void callWorkflow() throws EngineException {
        try {
            copper2GoEngine.start(dependencyInjector);
            Assertions
                    .assertThatNoException()
                    .isThrownBy(() -> copper2GoEngine.callWorkflow("", null, "Hello", 1L, 0L));
        } finally {
            copper2GoEngine.stop();
        }
    }


    public static Copper2GoEngine createCopper2GoEngine() {
        WorkflowRepositoryConfig workflowRepositoryConfig = new WorkflowRepositoryConfig(
                "release/2",
                "https://github.com/Keymaster65/copper2go-workflows.git",
                "/src/workflow/java"
        );
        return new Copper2GoEngineImpl(
                10,
                workflowRepositoryConfig,
                replyChannelStoreImpl
                );
    }
}