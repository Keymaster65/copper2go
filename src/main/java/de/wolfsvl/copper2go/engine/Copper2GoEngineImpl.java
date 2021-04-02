package de.wolfsvl.copper2go.engine;

import de.wolfsvl.copper2go.impl.ContextStoreImpl;
import de.wolfsvl.copper2go.impl.DefaultDependencyInjector;
import de.wolfsvl.copper2go.workflowapi.Context;
import de.wolfsvl.copper2go.workflowapi.ContextStore;
import de.wolfsvl.copper2go.workflowapi.HelloData;
import org.copperengine.core.CopperException;
import org.copperengine.core.DependencyInjector;
import org.copperengine.core.EngineState;
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

import javax.management.InstanceAlreadyExistsException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanRegistrationException;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import java.io.File;
import java.util.Collections;
import java.util.concurrent.locks.LockSupport;

public class Copper2GoEngineImpl implements Copper2GoEngine {

    private String branch = "master";
    private String workflowGitURI = "https://github.com/Keymaster65/copper2go-workflows.git";
    private String workflowBase = "/src/workflow/java";
    private static final Logger log = LoggerFactory.getLogger(Copper2GoEngineImpl.class);

    private TransientScottyEngine engine;
    private SimpleJmxExporter exporter;
    private LoggingStatisticCollector statisticsCollector;
    private int availableTickets = 10;

    private ContextStore contextStore;

    public Copper2GoEngineImpl(final String[] args) {
        if (args != null && args.length > 0) {
            workflowGitURI = args[0];
        }
        if (args != null && args.length > 1) {
            this.branch = args[1];
        }
        if (args != null && args.length > 2) {
            this.workflowBase = args[2];
        }
    }

    public void callWorkflow(final Context context) throws CopperException {
        WorkflowInstanceDescr<HelloData> workflowInstanceDescr = new WorkflowInstanceDescr<>("Hello");
        WorkflowVersion version = engine.getWfRepository().findLatestMinorVersion(workflowInstanceDescr.getWfName(), 1, 0);
        workflowInstanceDescr.setVersion(version);

        String uuid = engine.createUUID();
        workflowInstanceDescr.setData(new HelloData(uuid));
        contextStore.store(uuid, context);
        engine.run(workflowInstanceDescr);
    }

    public TransientScottyEngine start(DependencyInjector dependencyInjector) throws EngineException {
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
                    repo.addSourceDir(getWorkflowSourceDirectory().getAbsolutePath() + workflowBase);
                    repo.setTargetDir(workDir + "/target");
                    repo.setBranch(Copper2GoEngineImpl.this.branch);
                    repo.setOriginURI(workflowGitURI);
                    return repo;
                } catch (Exception createException) {
                    throw new EngineRuntimeException("Exception while creating workflow rfepository.", createException);
                }
            }
        };
        final TransientScottyEngine transientScottyEngine = factory.create();
        while (!transientScottyEngine.getEngineState().equals(EngineState.STARTED)) ;
        exporter = startJmxExporter(transientScottyEngine);
        return transientScottyEngine;
    }

    private SimpleJmxExporter startJmxExporter(TransientScottyEngine engine) throws EngineException {
        SimpleJmxExporter exporter = new SimpleJmxExporter();
        exporter.addProcessingEngineMXBean("demo-engine", engine);
        exporter.addWorkflowRepositoryMXBean("demo-workflow-repository", (FileBasedWorkflowRepository) engine.getWfRepository());
        engine.getProcessorPools().forEach(pool -> exporter.addProcessorPoolMXBean(pool.getId(), pool));

        statisticsCollector = new LoggingStatisticCollector();
        statisticsCollector.start();
        engine.setStatisticsCollector(statisticsCollector);
        exporter.addStatisticsCollectorMXBean("hello-statistics", statisticsCollector);

        try {
            exporter.startup();
        } catch (Exception e) {
           throw new EngineException("Failed to start JMX exporter.", e);
        }
        return exporter;
    }

    public void waitForIdleEngine() {
        while (engine.getNumberOfWorkflowInstances() > 0) {
            LockSupport.parkNanos(100000000L);
        }
    }

    public synchronized void start() throws EngineException {
        log.info("start engine");
        contextStore = new ContextStoreImpl();
        engine = start(new DefaultDependencyInjector(contextStore));
    }

    public void stop() throws EngineException {
        engine.shutdown();
        statisticsCollector.shutdown();
        try {
            exporter.shutdown();
        } catch (MBeanRegistrationException | InstanceNotFoundException e) {
            throw new EngineException("Could not stop engine.", e);
        }
        waitForIdleEngine();
    }
}
