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
package io.github.keymaster65.copper2go.engine.vanilla.impl;

import io.github.keymaster65.copper2go.api.workflow.EventChannelStore;
import io.github.keymaster65.copper2go.api.workflow.RequestChannelStore;
import io.github.keymaster65.copper2go.engine.Copper2GoEngine;
import io.github.keymaster65.copper2go.engine.ReplyChannelStoreImpl;
import io.github.keymaster65.copper2go.engine.vanilla.workflowapi.Workflow;
import io.github.keymaster65.copper2go.engine.vanilla.workflowapi.WorkflowFactoryFactory;

import java.util.concurrent.Executors;

public class Copper2GoEngineFactory {

    public static Copper2GoEngine create(
            final ReplyChannelStoreImpl replyChannelStoreImpl,
            final RequestChannelStore requestChannelStore,
            final EventChannelStore eventChannelStore,
            final FutureStore<Workflow> workflowStore,
            final ExpectedResponsesStore expectedResponsesStore,
            final WorkflowFactoryFactory workflowFactoryFactory
    ) {
        final FutureStore<Continuation> continuationStore = new FutureStore<>(Continuation.class);
        final VanillaEngineImpl vanillaEngineImpl = new VanillaEngineImpl(
                replyChannelStoreImpl,
                requestChannelStore,
                eventChannelStore,
                Executors.newFixedThreadPool(16),
                continuationStore,
                expectedResponsesStore
        );

        return new Copper2GoEngine(
                new PayloadReceiverImpl(
                        vanillaEngineImpl,
                        workflowStore,
                        workflowFactoryFactory.create(vanillaEngineImpl)
                ),
                new ResponseReceiverImpl(vanillaEngineImpl),
                new EngineControlImpl(vanillaEngineImpl, workflowStore, continuationStore)
        );
    }

    private Copper2GoEngineFactory() {
    }
}
