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

import io.github.keymaster65.copper2go.engine.EngineControl;
import io.github.keymaster65.copper2go.engine.EngineException;
import io.github.keymaster65.copper2go.engine.EngineRuntimeException;
import io.github.keymaster65.copper2go.engine.WorkflowRepositoryConfig;
import org.copperengine.core.DependencyInjector;
import org.copperengine.core.EngineState;
import org.copperengine.core.common.DefaultTicketPoolManager;
import org.copperengine.core.common.SimpleJmxExporter;
import org.copperengine.core.common.TicketPool;
import org.copperengine.core.common.TicketPoolManager;
import org.copperengine.core.common.WorkflowRepository;
import org.copperengine.core.monitoring.LoggingStatisticCollector;
import org.copperengine.core.tranzient.TransientEngineFactory;
import org.copperengine.core.tranzient.TransientScottyEngine;
import org.copperengine.core.wfrepo.FileBasedWorkflowRepository;
import org.copperengine.ext.wfrepo.git.GitWorkflowRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.management.InstanceNotFoundException;
import javax.management.MBeanRegistrationException;
import java.io.File;
import java.util.Collections;
import java.util.concurrent.locks.LockSupport;

public class EngineControlImpl implements EngineControl {

    final TransientScottyEngine scottyEngine;

    private static final Logger log = LoggerFactory.getLogger(EngineControlImpl.class);
    private LoggingStatisticCollector statisticsCollector;
    private SimpleJmxExporter exporter;

    public EngineControlImpl(
            final int availableTickets,
            final WorkflowRepositoryConfig workflowRepositoryConfig
    ) {
        this.scottyEngine = createScotty(availableTickets, workflowRepositoryConfig);
    }

    public synchronized void start(final DependencyInjector dependencyInjector) throws EngineException {
        log.info("start engine");
        scottyEngine.setDependencyInjector(dependencyInjector);
        startScotty();
    }


    @Override
    public synchronized void stop() throws EngineException {
        try {
            if (scottyEngine.getEngineState().equals(EngineState.STARTED)) {
                scottyEngine.shutdown();
            }
            if (statisticsCollector != null) {
                statisticsCollector.shutdown();
            }
            if (exporter != null) {
                exporter.shutdown();
            }
        } catch (MBeanRegistrationException | InstanceNotFoundException e) {
            throw new EngineException("Could not shutdown engine.", e);
        }
        waitForIdleEngine();
    }

    private TransientScottyEngine createScotty(
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

    private void startScotty() throws EngineException {
        scottyEngine.startup();
        while (!scottyEngine.getEngineState().equals(EngineState.STARTED)) {
            LockSupport.parkNanos(10L * 1000L * 1000L);
        }

        exporter = startJmxExporter(scottyEngine);
    }

    private SimpleJmxExporter startJmxExporter(final TransientScottyEngine engine) throws EngineException {
        var newExporter = new SimpleJmxExporter();
        newExporter.addProcessingEngineMXBean("copper2go-engine", engine);
        newExporter.addWorkflowRepositoryMXBean("copper2go-workflow-repository", (FileBasedWorkflowRepository) engine.getWfRepository());
        engine.getProcessorPools().forEach(pool -> newExporter.addProcessorPoolMXBean(pool.getId(), pool));

        statisticsCollector = new LoggingStatisticCollector();
        statisticsCollector.start();
        engine.setStatisticsCollector(statisticsCollector);
        newExporter.addStatisticsCollectorMXBean("copper2go-statistics", statisticsCollector);

        try {
            newExporter.startup();
        } catch (Exception e) {
            throw new EngineException("Failed to start JMX exporter.", e);
        }
        return newExporter;
    }

    private void waitForIdleEngine() {
        while (scottyEngine != null && scottyEngine.getNumberOfWorkflowInstances() > 0) {
            LockSupport.parkNanos(100000000L);
        }
    }
}
