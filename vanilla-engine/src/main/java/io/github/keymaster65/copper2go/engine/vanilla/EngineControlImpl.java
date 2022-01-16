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

public class EngineControlImpl implements EngineControl {

    private final VanillaEngineImpl vanillaEngineImpl;

    public EngineControlImpl(final VanillaEngineImpl vanillaEngineImpl) {
        this.vanillaEngineImpl = vanillaEngineImpl;
    }

    @Override
    public void start() throws EngineException {
        if (vanillaEngineImpl.executorService == null) {
            throw new EngineException("VanillaEngine has no executorService.");
        }
    }

    @Override
    public void stop() throws EngineException {
        if (vanillaEngineImpl.executorService == null) {
            throw new EngineException("VanillaEngine has no executorService.");
        }
        vanillaEngineImpl.executorService.shutdown();
    }
}
