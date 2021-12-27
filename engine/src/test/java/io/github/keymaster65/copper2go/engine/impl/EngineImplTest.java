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

import io.github.keymaster65.copper2go.engine.Engine;
import io.github.keymaster65.copper2go.engine.EngineException;
import io.github.keymaster65.copper2go.engine.WorkflowRepositoryConfig;
import io.github.keymaster65.copper2go.util.Copper2goDependencyInjector;
import org.assertj.core.api.Assertions;
import org.copperengine.core.DependencyInjector;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class EngineImplTest {

    private static Engine engine;
    private static ReplyChannelStoreImpl replyChannelStoreImpl = new ReplyChannelStoreImpl();
    private final DependencyInjector dependencyInjector = new Copper2goDependencyInjector(
            replyChannelStoreImpl,
            null,
            null
    );

    @BeforeAll
    static void createEngine() {
        engine = createCopper2GoEngine();
    }

    @Test
    void receiveInitialPayloadEngineNotStarted() {
        Assertions
                .assertThatExceptionOfType(NullPointerException.class)
                .isThrownBy(() -> engine.receive("", null, "Hello", 1L, 0L))
                .withMessage("No engine found. May be it must be started first.");
    }

    @Test
    void receiveInitialPayload() throws EngineException {
        try {
            engine.start(dependencyInjector);
            Assertions
                    .assertThatNoException()
                    .isThrownBy(() -> engine.receive("", null, "Hello", 1L, 0L));
        } finally {
            engine.stop();
        }
    }


    public static Engine createCopper2GoEngine() {
        WorkflowRepositoryConfig workflowRepositoryConfig = new WorkflowRepositoryConfig(
                "release/2",
                "https://github.com/Keymaster65/copper2go-workflows.git",
                "/src/workflow/java"
        );
        return new EngineImpl(
                10,
                workflowRepositoryConfig,
                replyChannelStoreImpl
                );
    }
}