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
import java.util.concurrent.Future;

class FutureHandler {

    private static final Logger log = LoggerFactory.getLogger(FutureHandler.class);

    private final Map<Future<?>, Workflow> observables;

    FutureHandler(final Map<Future<?>, Workflow> observables) {
        this.observables = observables;
    }

    void handleDone() {
        observables
                .keySet()
                .stream()
                .filter(Future::isDone)
                .forEach(this::get);
    }

    void get(final Future<?> voidFuture) {
        try {
            voidFuture.get();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.warn("InterruptedException caught in workflowInstance {}.", observables.get(voidFuture), e);
        } catch (Exception e) {
            log.warn("Exception caught in workflowInstance {}.", observables.get(voidFuture), e);
        } finally {
            final Workflow workflow = observables.remove(voidFuture);
            log.info("Removed workflowInstance {}.", workflow);
        }
    }
}
