/*
 * Copyright 2019 Wolf Sluyterman van Langeweyde
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
package de.wolfsvl.copper2go.application;

import de.wolfsvl.copper2go.impl.ContextStoreImpl;
import de.wolfsvl.copper2go.impl.DefaultDependencyInjector;
import de.wolfsvl.copper2go.impl.StdInOutContextImpl;
import de.wolfsvl.copper2go.vertx.VertxHttpServer;
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

import javax.management.InstanceNotFoundException;
import javax.management.MBeanRegistrationException;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.concurrent.locks.LockSupport;

public class Application {
    private static final Logger log = LoggerFactory.getLogger(Application.class);
    private String branch = "master";
    private String workflowGitURI = "https://github.com/Keymaster65/copper2go-workflows.git";
    private String workflowBase = "/src/workflow/java";

    private TransientScottyEngine engine;
    private SimpleJmxExporter exporter;
    private LoggingStatisticCollector statisticsCollector;
    private int availableTickets = 10;

    private ContextStore contextStore;
    private Copper2GoHttpServer httpServer;


    public Application(final String[] args) {
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

    public static void main(String[] args) throws Exception {
        Application application = null;
        try {
            log.info("begin application");
            application = new Application(args);
            application.startup();
        } catch (Exception e) {
            log.error("Exception in application main.", e);
        } finally {
            application.shutdown();
        }
        log.info("finished application main");
    }

    private void listenLocalStream() {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        while (1 == 1) {
            try {
                System.out.println("Enter your name: ");
                String line1 = reader.readLine();
                log.debug("line1=" + line1);
                if (line1 == null) {
                    throw new NullPointerException("Read a 'null' line. So there seems to be no stdin. Might happen when starting with gradle.");
                }
                if ("exit".equals(line1)) {
                    throw new ApplicationException("Input canceled by 'exit' line.");
                }
                callWorkflow(new StdInOutContextImpl(line1));
                waitForIdleEngine();
            } catch (Exception e) {
                throw new ApplicationException("Exception while getting input.", e);
            }
        }
    }

    public void callWorkflow(final Context context) throws CopperException {
        WorkflowInstanceDescr workflowInstanceDescr = new WorkflowInstanceDescr<HelloData>("Hello");
        log.debug("workflowInstanceDescr=" + workflowInstanceDescr);
        WorkflowVersion version = engine.getWfRepository().findLatestMinorVersion(workflowInstanceDescr.getWfName(), 1, 0);
        log.debug("version=" + version);
        workflowInstanceDescr.setVersion(version);

        String uuid = engine.createUUID();
        workflowInstanceDescr.setData(new HelloData(uuid));
        contextStore.store(uuid, context);
        engine.run(workflowInstanceDescr);
    }

    public synchronized void startup() throws Exception {
        log.info("start application");
        contextStore = new ContextStoreImpl();

        engine = startEngine(new DefaultDependencyInjector(contextStore));
        startHttpServer();
        listenLocalStream();
    }

    public synchronized void shutdown() throws InstanceNotFoundException, MBeanRegistrationException {
        log.info("shutdown application");
        try {
            httpServer.stop();
        } catch (Exception e) {
            log.warn("Exception while stopping HTTP server.", e);
        }
        stopEngine();
    }


    private void startHttpServer() throws Exception {
        httpServer = new VertxHttpServer(8080, this);
        httpServer.start();
    }

    private TransientScottyEngine startEngine(DependencyInjector dependencyInjector) throws Exception {
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
                    new File("workDir").mkdirs();
                    GitWorkflowRepository repo = new GitWorkflowRepository();

                    repo.setGitRepositoryDir(getWorkflowSourceDirectory());
                    repo.addSourceDir(getWorkflowSourceDirectory().getAbsolutePath() + workflowBase);
                    repo.setTargetDir(workDir + "/target");
                    repo.setBranch(Application.this.branch);
                    repo.setOriginURI(workflowGitURI);
                    return repo;
                } catch (Exception createException) {
                    throw new RuntimeException("Exception while creating workflow rfepository.", createException);
                }
            }
        };
        final TransientScottyEngine transientScottyEngine = factory.create();
        while (!transientScottyEngine.getEngineState().equals(EngineState.STARTED)) ;
        exporter = startJmxExporter(transientScottyEngine);
        return transientScottyEngine;
    }

    private void stopEngine() throws MBeanRegistrationException, InstanceNotFoundException {
        engine.shutdown();
        statisticsCollector.shutdown();
        exporter.shutdown();
        waitForIdleEngine();
    }

    private void waitForIdleEngine() {
        while (engine.getNumberOfWorkflowInstances() > 0) {
            LockSupport.parkNanos(100000000L);
        }
    }

    private SimpleJmxExporter startJmxExporter(TransientScottyEngine engine) throws Exception {
        SimpleJmxExporter exporter = new SimpleJmxExporter();
        exporter.addProcessingEngineMXBean("demo-engine", engine);
        exporter.addWorkflowRepositoryMXBean("demo-workflow-repository", (FileBasedWorkflowRepository) engine.getWfRepository());
        engine.getProcessorPools().forEach(pool -> exporter.addProcessorPoolMXBean(pool.getId(), pool));

        statisticsCollector = new LoggingStatisticCollector();
        statisticsCollector.start();
        engine.setStatisticsCollector(statisticsCollector);
        exporter.addStatisticsCollectorMXBean("hello-statistics", statisticsCollector);

        exporter.startup();
        return exporter;
    }

    public static class ApplicationException extends RuntimeException {
        public ApplicationException(String message, Exception cause) {
            super(message, cause);
        }

        public ApplicationException(String message) {
            super(message);
        }
    }
}