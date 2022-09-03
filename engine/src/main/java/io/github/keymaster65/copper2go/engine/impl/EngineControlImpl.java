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
import io.github.keymaster65.copper2go.api.connector.EngineException;
import io.github.keymaster65.copper2go.engine.WorkflowRepositoryConfig;
import org.copperengine.core.DependencyInjector;
import org.copperengine.core.EngineState;
import org.copperengine.core.common.SimpleJmxExporter;
import org.copperengine.core.monitoring.LoggingStatisticCollector;
import org.copperengine.core.tranzient.TransientScottyEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.management.InstanceNotFoundException;
import javax.management.MBeanRegistrationException;
import java.util.concurrent.locks.LockSupport;

public class EngineControlImpl implements EngineControl {

    final TransientScottyEngine scottyEngine;

    private static final Logger log = LoggerFactory.getLogger(EngineControlImpl.class);

    private final DependencyInjector dependencyInjector;
    private final LoggingStatisticCollector statisticsCollector;
    private final SimpleJmxExporter exporter;

    public EngineControlImpl(
            final int availableTickets,
            final WorkflowRepositoryConfig workflowRepositoryConfig,
            final DependencyInjector dependencyInjector
    ) {
        scottyEngine = ScottyFactory.create(availableTickets, workflowRepositoryConfig);
        statisticsCollector = new LoggingStatisticCollector();
        exporter = SimpleJmxExporterFactory.create(scottyEngine, statisticsCollector);
        scottyEngine.setStatisticsCollector(statisticsCollector);
        this.dependencyInjector = dependencyInjector;
    }

    EngineControlImpl(
            final TransientScottyEngine scottyEngine,
            final LoggingStatisticCollector statisticsCollector,
            final SimpleJmxExporter exporter,
            final DependencyInjector dependencyInjector
    ) {
        this.scottyEngine = scottyEngine;
        this.statisticsCollector = statisticsCollector;
        this.exporter = exporter;
        this.dependencyInjector = dependencyInjector;
    }

    @Override
    public synchronized void start() throws EngineException {
        log.info("start engine");
        scottyEngine.setDependencyInjector(dependencyInjector);

        startScotty();
    }


    @Override
    public synchronized void stop() throws EngineException {
        shutdown(scottyEngine, statisticsCollector);
        shutdownExporter(exporter);
        waitForIdleEngine(scottyEngine);
    }

    static void shutdown(final TransientScottyEngine scottyEngine, final LoggingStatisticCollector statisticsCollector) {
        if (scottyEngine.getEngineState().equals(EngineState.STARTED)) {
            scottyEngine.shutdown();
            statisticsCollector.shutdown();
        }
    }

    static void shutdownExporter(final SimpleJmxExporter exporter) throws EngineException {
        try {
            exporter.shutdown();
        } catch (MBeanRegistrationException | InstanceNotFoundException e) {
            throw new EngineException("Could not shutdown engine.", e);
        }
    }

    private void startScotty() throws EngineException {
        scottyEngine.startup();
        while (!scottyEngine.getEngineState().equals(EngineState.STARTED)) {
            LockSupport.parkNanos(10L * 1000L * 1000L);
        }

        statisticsCollector.start();
        startJmxExporter(exporter);
    }

    static void startJmxExporter(final SimpleJmxExporter exporter) throws EngineException {

        try {
            exporter.startup();
        } catch (Exception e) {
            throw new EngineException("Failed to start JMX exporter.", e);
        }
    }

    static void waitForIdleEngine(final TransientScottyEngine scottyEngine) {
        while (scottyEngine.getNumberOfWorkflowInstances() > 0) {
            LockSupport.parkNanos(100000000L);
        }
    }
}
