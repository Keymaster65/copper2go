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
import io.github.keymaster65.copper2go.engine.EngineRuntimeException;
import io.github.keymaster65.copper2go.engine.ReplyChannel;
import io.github.keymaster65.copper2go.engine.WorkflowRepositoryConfig;
import io.github.keymaster65.copper2go.workflowapi.WorkflowData;
import org.copperengine.core.Acknowledge;
import org.copperengine.core.CopperException;
import org.copperengine.core.DependencyInjector;
import org.copperengine.core.EngineState;
import org.copperengine.core.Response;
import org.copperengine.core.WorkflowInstanceDescr;
import org.copperengine.core.WorkflowVersion;
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
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.locks.LockSupport;

public class EngineImpl implements Engine {


    private static final Logger log = LoggerFactory.getLogger(EngineImpl.class);

    private final WorkflowRepositoryConfig workflowRepositoryConfig;
    private final ReplyChannelStoreImpl replyChannelStore;
    private final int availableTickets;

    private TransientScottyEngine engine;
    private SimpleJmxExporter exporter;
    private LoggingStatisticCollector statisticsCollector;

    public EngineImpl(final int availableTickets, WorkflowRepositoryConfig workflowRepositoryConfig, final ReplyChannelStoreImpl replyChannelStore) {
        this.workflowRepositoryConfig = workflowRepositoryConfig;
        this.replyChannelStore = replyChannelStore;
        this.availableTickets = availableTickets;
    }

    @Override
    public void receive(
            final String payload,
            final Map<String, String> attributes,
            final ReplyChannel replyChannel,
            final String workflow,
            final long major,
            final long minor
    ) throws EngineException {
        Objects.requireNonNull(engine, "No engine found. May be it must be started first.");

        WorkflowInstanceDescr<WorkflowData> workflowInstanceDescr = new WorkflowInstanceDescr<>(workflow);
        WorkflowVersion version = engine.getWfRepository().findLatestMinorVersion(workflowInstanceDescr.getWfName(), major, minor);
        workflowInstanceDescr.setVersion(version);

        String uuid = engine.createUUID();
        workflowInstanceDescr.setData(new WorkflowData(uuid, payload, attributes));
        replyChannelStore.store(uuid, replyChannel);
        try {
            engine.run(workflowInstanceDescr);
        } catch (CopperException e) {
            throw new EngineException("Exception while running workflow. ", e);
        }
    }

    private TransientScottyEngine startScotty(DependencyInjector dependencyInjector) throws EngineException {
        var factory = new TransientEngineFactory() {
            @Override
            protected File getWorkflowSourceDirectory() {
                return new File("./.copper/clone");
            }

            @Override
            protected DependencyInjector createDependencyInjector() {
                return dependencyInjector;
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
        };
        final var transientScottyEngine = factory.create();
        while (!transientScottyEngine.getEngineState().equals(EngineState.STARTED)) {
            LockSupport.parkNanos(10L * 1000L * 1000L);
        }

        exporter = startJmxExporter(transientScottyEngine);
        return transientScottyEngine;
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

    @Override
    public void waitForIdleEngine() {
        while (engine != null && engine.getNumberOfWorkflowInstances() > 0) {
            LockSupport.parkNanos(100000000L);
        }
    }

    @Override
    public void notify(final String correlationId, final String response) {
        Response<String> copperResponse = new Response<>(correlationId, response, null);
        engine.notify(copperResponse, new Acknowledge.BestEffortAcknowledge());
    }

    @Override
    public void notifyError(final String correlationId, final String response) {
        Response<String> copperResponse = new Response<>(correlationId, response, new RuntimeException(response));
        engine.notify(copperResponse, new Acknowledge.BestEffortAcknowledge());
    }

    public synchronized void start(final DependencyInjector dependencyInjector) throws EngineException {
        log.info("start engine");
        engine = startScotty(dependencyInjector);
    }

    public synchronized void stop() throws EngineException {
        if (engine != null) {
            engine.shutdown();
        }
        if (statisticsCollector != null) {
            statisticsCollector.shutdown();
        }

        try {
            if (exporter != null) {
                exporter.shutdown();
            }
        } catch (MBeanRegistrationException | InstanceNotFoundException e) {
            throw new EngineException("Could not shutdown exporter.", e);
        }
        waitForIdleEngine();
        engine = null;
    }
}
