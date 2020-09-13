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
package de.wolfsvl.copper2go;

import de.wolfsvl.copper2go.impl.ContextStoreImpl;
import de.wolfsvl.copper2go.impl.DefaultDependencyInjector;
import de.wolfsvl.copper2go.impl.HttpContextImpl;
import de.wolfsvl.copper2go.workflowapi.Context;
import de.wolfsvl.copper2go.impl.StdInOutContextImpl;
import de.wolfsvl.copper2go.workflowapi.ContextStore;
import de.wolfsvl.copper2go.workflowapi.HelloData;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpServerRequest;
import org.copperengine.core.*;
import org.copperengine.core.common.*;
import org.copperengine.core.monitoring.LoggingStatisticCollector;
import org.copperengine.core.tranzient.TransientEngineFactory;
import org.copperengine.core.tranzient.TransientScottyEngine;
import org.copperengine.core.wfrepo.FileBasedWorkflowRepository;
import org.copperengine.ext.wfrepo.git.GitWorkflowRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import javax.management.InstanceNotFoundException;
import javax.management.MBeanRegistrationException;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.concurrent.locks.LockSupport;

public class Application {
    private static final Logger log = LoggerFactory.getLogger(Application.class);
    private  String branch = "master";
    private String workflowGitURI = "https://github.com/Keymaster65/copper2go-workflows.git";
    private String workflowBase = "/src/workflow/java";

    private TransientScottyEngine engine;
    private SimpleJmxExporter exporter;
    private LoggingStatisticCollector statisticsCollector;

    private ContextStore contextStore;
    private int availableTickets = 10;
    private Copper2GoVerticle verticle;

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
            application.start();
            application.listenLocalStream();
        } catch (Exception e) {
            log.error("Exception in applications main.", e);
        } finally {
            application.shutdown();
        }
        log.info("finished application");
    }

    private void listenLocalStream() throws CopperException, FileNotFoundException {
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

    private void callWorkflow(final Context context) throws CopperException {
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

    public void start() throws Exception {
        log.info("start application");
        contextStore = new ContextStoreImpl();
        engine = startEngine(new DefaultDependencyInjector(contextStore));
        while (!engine.getEngineState().equals(EngineState.STARTED)) ;
        startHttpServer();
        exporter = startJmxExporter();
    }

    private void startHttpServer() throws Exception {
        Vertx vertx = Vertx.vertx();

        verticle = new Copper2GoVerticle(new Handler<HttpServerRequest>() {
            @Override
            public void handle(HttpServerRequest request) {
                request.handler(new Handler<Buffer>() {
                    @Override
                    public void handle(Buffer buffer) {

                        final String requestBody = new String(buffer.getBytes(), StandardCharsets.UTF_8);
                        try {
                            callWorkflow(new HttpContextImpl(requestBody, request.response()));
                        } catch (CopperException e) {
                            // TODO
                            e.printStackTrace();
                        }
                    }
                });
            }
        });


        vertx.deployVerticle(verticle);
        verticle.start();
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
        return factory.create();
    }

    public void shutdown() throws InstanceNotFoundException, MBeanRegistrationException {
        log.info("shutdown application");
        waitForIdleEngine();
        engine.shutdown();
        statisticsCollector.shutdown();
        exporter.shutdown();
        try {
            verticle.stop();
        } catch (Exception e) {
            throw new ApplicationException("Can not stop verticle.", e);
        }
    }

    private void waitForIdleEngine() {
        while (engine.getNumberOfWorkflowInstances() > 0) {
            LockSupport.parkNanos(100000000L);
        }
    }

    private SimpleJmxExporter startJmxExporter() throws Exception {
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