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
package io.github.keymaster65.copper2go.engine.scotty;

import io.github.keymaster65.copper2go.engine.Copper2GoEngine;
import io.github.keymaster65.copper2go.engine.ReplyChannelStoreImpl;
import org.copperengine.core.DependencyInjector;

public class Copper2GoEngineFactory {

    private Copper2GoEngineFactory() {
    }

    public static Copper2GoEngine create(
            final int availableTickets,
            final WorkflowRepositoryConfig workflowRepositoryConfig,
            final ReplyChannelStoreImpl replyChannelStore,
            final DependencyInjector dependencyInjector
    ) {
        final EngineControlImpl engineControl = new EngineControlImpl(availableTickets, workflowRepositoryConfig, dependencyInjector);
        return new Copper2GoEngine(
                new PayloadReceiverImpl(engineControl.scottyEngine, replyChannelStore),
                new ResponseReceiverImpl(engineControl.scottyEngine),
                engineControl
        );

    }
}
