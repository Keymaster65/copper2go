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
package io.github.keymaster65.copper2go.engine;

import org.copperengine.core.DependencyInjector;

public interface Copper2GoEngine {
    void start(DependencyInjector dependencyInjector) throws EngineException;

    void stop() throws EngineException;

    void callWorkflow(
            final String payload,
            final ReplyChannel replyChannel,
            final String workflow,
            final long major,
            final long minor
    ) throws EngineException;

    void waitForIdleEngine();

    void notify(final String correlationId, String response);

    void notifyError(final String correlationId, String response);
}
