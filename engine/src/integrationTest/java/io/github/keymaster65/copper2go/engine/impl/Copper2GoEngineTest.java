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

import io.github.keymaster65.copper2go.api.connector.EngineException;
import io.github.keymaster65.copper2go.engine.WorkflowRepositoryConfig;
import io.github.keymaster65.copper2go.api.util.Copper2goDependencyInjector;
import org.assertj.core.api.Assertions;
import org.copperengine.core.DependencyInjector;
import org.copperengine.ext.wfrepo.git.GitWorkflowRepository;
import org.junit.jupiter.api.Test;

class Copper2GoEngineTest {

    @Test
    void startStop() throws EngineException {
        Copper2GoEngine engine = createStartedEngine("release/3");
        Assertions
                .assertThatNoException()
                .isThrownBy(() -> engine.getEngineControl().close());
        Assertions
                .assertThatCode(() -> engine.getEngineControl().close())
                .isInstanceOf(EngineException.class);
    }


    @Test
    void badBranch() {
        Assertions
                .assertThatCode(() -> createStartedEngine("notExisting"))
                .isInstanceOf(GitWorkflowRepository.GitWorkflowRepositoryException.class);
    }

    private static Copper2GoEngine createStartedEngine(final String branch) throws EngineException {
        final ReplyChannelStoreImpl replyChannelStoreImpl = new ReplyChannelStoreImpl();
        final DependencyInjector dependencyInjector = new Copper2goDependencyInjector(
                replyChannelStoreImpl,
                null,
                null
        );
        Copper2GoEngine engine = createEngine(branch);
        engine.getEngineControl().start(dependencyInjector);

        return engine;
    }

    private static Copper2GoEngine createEngine(final String branch) {
        final ReplyChannelStoreImpl replyChannelStoreImpl = new ReplyChannelStoreImpl();
        WorkflowRepositoryConfig workflowRepositoryConfig = new WorkflowRepositoryConfig(
                branch,
                "https://github.com/Keymaster65/copper2go-workflows.git",
                "/src/workflow/java"
        );
        return new Copper2GoEngine(
                10,
                workflowRepositoryConfig,
                replyChannelStoreImpl
        );
    }
}