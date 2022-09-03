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
import org.assertj.core.api.Assertions;
import org.copperengine.core.DependencyInjector;
import org.copperengine.core.EngineState;
import org.copperengine.core.common.SimpleJmxExporter;
import org.copperengine.core.monitoring.LoggingStatisticCollector;
import org.copperengine.core.tranzient.TransientScottyEngine;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import javax.management.InstanceAlreadyExistsException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanRegistrationException;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;

class EngineControlImplTest {

    @Test
    void start() throws EngineException, MalformedObjectNameException, NotCompliantMBeanException, InstanceAlreadyExistsException, MBeanRegistrationException {
        final TransientScottyEngine scottyEngine = Mockito.mock(TransientScottyEngine.class);
        final LoggingStatisticCollector statisticsCollector = Mockito.mock(LoggingStatisticCollector.class);
        final SimpleJmxExporter exporter = Mockito.mock(SimpleJmxExporter.class);
        final EngineControlImpl engineControl = new EngineControlImpl(
                scottyEngine,
                statisticsCollector,
                exporter,
                Mockito.mock(DependencyInjector.class)
        );
        Mockito.when(scottyEngine.getEngineState())
                .thenReturn(EngineState.RAW)
                .thenReturn(EngineState.STARTED);

        engineControl.start();

        Mockito.verify(scottyEngine).startup();
        Mockito.verify(statisticsCollector).start();
        Mockito.verify(exporter).startup();
    }

    @Test
    void stop() throws EngineException, InstanceNotFoundException, MBeanRegistrationException {
        final TransientScottyEngine scottyEngine = Mockito.mock(TransientScottyEngine.class);
        final LoggingStatisticCollector statisticsCollector = Mockito.mock(LoggingStatisticCollector.class);
        final SimpleJmxExporter exporter = Mockito.mock(SimpleJmxExporter.class);
        final EngineControlImpl engineControl = new EngineControlImpl(
                scottyEngine,
                statisticsCollector,
                exporter,
                Mockito.mock(DependencyInjector.class)
        );
        Mockito.when(scottyEngine.getEngineState()).thenReturn(EngineState.STARTED);

        engineControl.stop();

        Mockito.verify(scottyEngine).shutdown();
        Mockito.verify(statisticsCollector).shutdown();
        Mockito.verify(exporter).shutdown();
    }

    @Test
    void startJmxExporter() throws EngineException, MalformedObjectNameException, NotCompliantMBeanException, InstanceAlreadyExistsException, MBeanRegistrationException {
        final SimpleJmxExporter jmxExporter = Mockito.mock(SimpleJmxExporter.class);
        EngineControlImpl.startJmxExporter(jmxExporter);

        Mockito.verify(jmxExporter).startup();
    }

    @Test
    void startJmxExporterException() throws MalformedObjectNameException, NotCompliantMBeanException, InstanceAlreadyExistsException, MBeanRegistrationException {
        final SimpleJmxExporter jmxExporter = Mockito.mock(SimpleJmxExporter.class);
        Mockito.doThrow(new NullPointerException("Test")).when(jmxExporter).startup();

        Assertions.assertThatCode(() -> EngineControlImpl.startJmxExporter(jmxExporter))
                .isInstanceOf(EngineException.class)
                .hasMessage("Failed to start JMX exporter.");
    }

    @Test
    void waitForIdleEngine() {
        final TransientScottyEngine scottyEngine = Mockito.mock(TransientScottyEngine.class);
        Mockito.when(scottyEngine.getNumberOfWorkflowInstances())
                .thenReturn(1)
                .thenReturn(0);


        Assertions.assertThatCode(() -> EngineControlImpl.waitForIdleEngine(scottyEngine))
                .doesNotThrowAnyException();
    }

    @Test
    void shutdownExporter() throws EngineException, InstanceNotFoundException, MBeanRegistrationException {
        final SimpleJmxExporter jmxExporter = Mockito.mock(SimpleJmxExporter.class);
        EngineControlImpl.shutdownExporter(jmxExporter);

        Mockito.verify(jmxExporter).shutdown();
    }

    @Test
    void shutdownJmxExporterException() throws InstanceNotFoundException, MBeanRegistrationException {
        final SimpleJmxExporter jmxExporter = Mockito.mock(SimpleJmxExporter.class);
        Mockito.doThrow(new InstanceNotFoundException("Test")).when(jmxExporter).shutdown();

        Assertions.assertThatCode(() -> EngineControlImpl.shutdownExporter(jmxExporter))
                .isInstanceOf(EngineException.class)
                .hasMessage("Could not shutdown engine.");
    }

    @Test
    void shutdownStartedEngine() {
        final TransientScottyEngine scottyEngine = Mockito.mock(TransientScottyEngine.class);
        final LoggingStatisticCollector statisticsCollector = Mockito.mock(LoggingStatisticCollector.class);
        Mockito.when(scottyEngine.getEngineState()).thenReturn(EngineState.STARTED);

        EngineControlImpl.shutdown(scottyEngine, statisticsCollector);

        Mockito.verify(scottyEngine).shutdown();
        Mockito.verify(statisticsCollector).shutdown();
    }

    @Test
    void shutdownRawEngine() {
        final TransientScottyEngine scottyEngine = Mockito.mock(TransientScottyEngine.class);
        final LoggingStatisticCollector statisticsCollector = Mockito.mock(LoggingStatisticCollector.class);
        Mockito.when(scottyEngine.getEngineState()).thenReturn(EngineState.RAW);

        EngineControlImpl.shutdown(scottyEngine, statisticsCollector);

        Mockito.verify(scottyEngine, Mockito.never()).shutdown();
        Mockito.verify(statisticsCollector, Mockito.never()).shutdown();
    }
}