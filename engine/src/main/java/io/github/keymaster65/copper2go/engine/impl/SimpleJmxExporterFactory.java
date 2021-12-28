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

import org.copperengine.core.common.SimpleJmxExporter;
import org.copperengine.core.monitoring.LoggingStatisticCollector;
import org.copperengine.core.tranzient.TransientScottyEngine;
import org.copperengine.core.wfrepo.FileBasedWorkflowRepository;

public class SimpleJmxExporterFactory {
    private SimpleJmxExporterFactory() {}

    static SimpleJmxExporter create(
            final TransientScottyEngine engine,
            final LoggingStatisticCollector loggingStatisticCollector
    ) {
        var newExporter = new SimpleJmxExporter();
        newExporter.addProcessingEngineMXBean("copper2go-engine", engine);
        newExporter.addWorkflowRepositoryMXBean("copper2go-workflow-repository", (FileBasedWorkflowRepository) engine.getWfRepository());
        newExporter.addStatisticsCollectorMXBean("copper2go-statistics", loggingStatisticCollector);
        engine.getProcessorPools().forEach(pool -> newExporter.addProcessorPoolMXBean(pool.getId(), pool));

        return newExporter;
    }
}
