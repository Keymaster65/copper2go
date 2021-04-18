package io.github.keymaster65.copper2go.engine.impl;

import io.github.keymaster65.copper2go.engine.Copper2GoEngine;
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
import java.util.concurrent.locks.LockSupport;

public class Copper2GoEngineImpl implements Copper2GoEngine {


    private static final Logger log = LoggerFactory.getLogger(Copper2GoEngineImpl.class);

    private final WorkflowRepositoryConfig workflowRepositoryConfig;
    private final ReplyChannelStoreImpl replyChannelStore;
    private final int availableTickets;

    private TransientScottyEngine engine;
    private SimpleJmxExporter exporter;
    private LoggingStatisticCollector statisticsCollector;

    public Copper2GoEngineImpl(final int availableTickets, WorkflowRepositoryConfig workflowRepositoryConfig, final ReplyChannelStoreImpl replyChannelStore) {
        this.workflowRepositoryConfig = workflowRepositoryConfig;
        this.replyChannelStore = replyChannelStore;
        this.availableTickets = availableTickets;
    }

    public void callWorkflow(
            final String payload,
            final ReplyChannel replyChannel,
            final String workflow,
            final long major,
            final long minor
    ) throws EngineException {
        WorkflowInstanceDescr<WorkflowData> workflowInstanceDescr = new WorkflowInstanceDescr<>(workflow);
        WorkflowVersion version = engine.getWfRepository().findLatestMinorVersion(workflowInstanceDescr.getWfName(), major, minor);
        workflowInstanceDescr.setVersion(version);

        String uuid = engine.createUUID();
        workflowInstanceDescr.setData(new WorkflowData(uuid, payload));
        replyChannelStore.store(uuid, replyChannel);
        try {
            engine.run(workflowInstanceDescr);
        } catch (CopperException e) {
            throw new EngineException("Exception while running workflow. ", e);
        }
    }

    public TransientScottyEngine startScotty(DependencyInjector dependencyInjector) throws EngineException {
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
                DefaultTicketPoolManager tpManager = new DefaultTicketPoolManager();
                tpManager.setTicketPools(Collections.singletonList(new TicketPool(DefaultTicketPoolManager.DEFAULT_POOL_ID, availableTickets)));
                return tpManager;
            }

            @Override
            protected WorkflowRepository createWorkflowRepository() {
                try {
                    String workDir = "./.copper";
                    if (!new File("workDir").mkdirs()) {
                        log.info("Could not create dir {}", "workDir");
                    }
                    GitWorkflowRepository repo = new GitWorkflowRepository();

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
        final TransientScottyEngine transientScottyEngine = factory.create();
        while (!transientScottyEngine.getEngineState().equals(EngineState.STARTED)) {
            LockSupport.parkNanos(10L * 1000L * 1000L);
        }

        exporter = startJmxExporter(transientScottyEngine);
        return transientScottyEngine;
    }

    private SimpleJmxExporter startJmxExporter(final TransientScottyEngine engine) throws EngineException {
        SimpleJmxExporter newExporter = new SimpleJmxExporter();
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

    public void waitForIdleEngine() {
        while (engine.getNumberOfWorkflowInstances() > 0) {
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
            exporter.shutdown();
        } catch (MBeanRegistrationException | InstanceNotFoundException e) {
            throw new EngineException("Could not shutdown exporter.", e);
        }
        waitForIdleEngine();
    }
}