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

import io.github.keymaster65.copper2go.api.connector.EngineException;
import io.github.keymaster65.copper2go.engine.EngineControl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.concurrent.locks.LockSupport;

public class EngineControlImpl implements EngineControl {

    private final VanillaEngineImpl vanillaEngineImpl;
    private final FutureStore<Workflow> workflowStore;
    private final FutureStore<Continuation> continuationStore;

    private static final Logger log = LoggerFactory.getLogger(EngineControlImpl.class);

    public EngineControlImpl(
            final VanillaEngineImpl vanillaEngineImpl,
            final FutureStore<Workflow> workflowStore,
            final FutureStore<Continuation> continuationStore
    ) {
        this.vanillaEngineImpl = vanillaEngineImpl;
        this.workflowStore = workflowStore;
        this.continuationStore = continuationStore;
    }

    @Override
    public void start() throws EngineException {
        if (vanillaEngineImpl.executorService == null) {
            throw new EngineException("VanillaEngine has no executorService.");
        }
        workflowStore.start();
        continuationStore.start();
    }

    @Override
    public void stop() throws EngineException {
        if (vanillaEngineImpl.executorService == null) {
            throw new EngineException("VanillaEngine has no executorService.");
        }
        vanillaEngineImpl.executorService.shutdown();
        while (workflowStore.size() + continuationStore.size() > 0) {
            log.debug("Wait for instances to shut down {}", workflowStore.size());
            LockSupport.parkNanos(Duration.ofMillis(100).toNanos());
        }
        continuationStore.stop();
        workflowStore.stop();
    }
}
