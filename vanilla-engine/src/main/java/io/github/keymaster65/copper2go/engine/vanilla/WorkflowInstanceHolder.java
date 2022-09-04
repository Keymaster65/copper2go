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
package io.github.keymaster65.copper2go.engine.vanilla;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class WorkflowInstanceHolder {

    public static final long INITIAL_DELAY = 0;
    public static final long PERIOD = 500;
    public static final TimeUnit TIME_UNIT = TimeUnit.MILLISECONDS;

    private static final Logger log = LoggerFactory.getLogger(WorkflowInstanceHolder.class);

    private final Map<Future<?>, Workflow> workflowInstances;
    private final ScheduledExecutorService futureHandlerService;
    private final FutureHandler<Workflow> futureHandler;

    public WorkflowInstanceHolder() {
        this(
                Executors.newSingleThreadScheduledExecutor(),
                new ConcurrentHashMap<>()
        );
    }

    WorkflowInstanceHolder(
            final ScheduledExecutorService futureHandlerService,
            final ConcurrentHashMap<Future<?>, Workflow> workflowInstances
    ) {
        this(futureHandlerService, workflowInstances, new FutureHandler<>(workflowInstances));
    }

    WorkflowInstanceHolder(
            final ScheduledExecutorService futureHandlerService,
            final Map<Future<?>, Workflow> workflowInstances,
            final FutureHandler<Workflow> futureHandler
    ) {
        this.futureHandlerService = futureHandlerService;
        this.workflowInstances = workflowInstances;
        this.futureHandler = futureHandler;
    }

    public synchronized void start() {
        final ScheduledFuture<?> scheduledFuture = futureHandlerService.scheduleAtFixedRate(
                futureHandler::handleDone,
                INITIAL_DELAY,
                PERIOD,
                TIME_UNIT
        );
        FutureObserver.create(scheduledFuture, "WorkflowObserver").start();
    }

    public synchronized void stop() {
        futureHandlerService.shutdown();
    }

    public void addFuture(final Future<?> workflowInstanceFuture, final Workflow workflowInstance) {
        log.debug("Add workflow instance {}.", workflowInstance);
        workflowInstances.put(workflowInstanceFuture, workflowInstance);
    }

    public long getWorkflowInstanceCount() {
        return workflowInstances.size();
    }
}
