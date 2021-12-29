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

import io.github.keymaster65.copper2go.connectorapi.EngineRuntimeException;
import io.github.keymaster65.copper2go.engine.WorkflowRepositoryConfig;
import org.copperengine.core.common.DefaultTicketPoolManager;
import org.copperengine.core.common.TicketPool;
import org.copperengine.core.common.TicketPoolManager;
import org.copperengine.core.common.WorkflowRepository;
import org.copperengine.core.tranzient.TransientEngineFactory;
import org.copperengine.core.tranzient.TransientScottyEngine;
import org.copperengine.ext.wfrepo.git.GitWorkflowRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Collections;

public class ScottyFactory {
    private static final Logger log = LoggerFactory.getLogger(ScottyFactory.class);

    private ScottyFactory() {}

    static TransientScottyEngine create(
            final int availableTickets,
            final WorkflowRepositoryConfig workflowRepositoryConfig
    ) {
        var factory = new TransientEngineFactory() {
            @Override
            protected File getWorkflowSourceDirectory() {
                return new File("./.copper/clone");
            }

            @Override
            protected TicketPoolManager createTicketPoolManager() {
                var tpManager = new DefaultTicketPoolManager();
                tpManager.setTicketPools(Collections.singletonList(new TicketPool(DefaultTicketPoolManager.DEFAULT_POOL_ID, availableTickets)));
                return tpManager;
            }

            @Override
            protected WorkflowRepository createWorkflowRepository() {
                try {
                    var workDir = "./.copper";
                    if (!new File("workDir").mkdirs()) {
                        log.info("Could not create dir {}", "workDir");
                    }
                    var repo = new GitWorkflowRepository();

                    repo.setGitRepositoryDir(getWorkflowSourceDirectory());
                    repo.addSourceDir(getWorkflowSourceDirectory().getAbsolutePath() + workflowRepositoryConfig.workflowBase);
                    repo.setTargetDir(workDir + "/target");
                    repo.setBranch(workflowRepositoryConfig.branch);
                    repo.setOriginURI(workflowRepositoryConfig.workflowGitURI);
                    return repo;
                } catch (Exception createException) {
                    throw new EngineRuntimeException("Exception while creating workflow rfepository.", createException);
                }
            }

            @Override
            public TransientScottyEngine create() {
                TransientScottyEngine transientScottyEngine = new TransientScottyEngine();
                transientScottyEngine.setEarlyResponseContainer(createEarlyResponseContainer());
                transientScottyEngine.setEngineIdProvider(createEngineIdProvider());
                transientScottyEngine.setIdFactory(createIdFactory());
                transientScottyEngine.setPoolManager(createProcessorPoolManager());
                transientScottyEngine.setStatisticsCollector(createRuntimeStatisticsCollector());
                transientScottyEngine.setTicketPoolManager(createTicketPoolManager());
                transientScottyEngine.setTimeoutManager(createTimeoutManager());
                transientScottyEngine.setWfRepository(createWorkflowRepository());
                return transientScottyEngine;
            }
        };
        return factory.create();
    }
}
