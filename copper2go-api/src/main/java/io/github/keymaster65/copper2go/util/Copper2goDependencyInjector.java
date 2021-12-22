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
package io.github.keymaster65.copper2go.util;

import io.github.keymaster65.copper2go.workflowapi.EventChannelStore;
import io.github.keymaster65.copper2go.workflowapi.ReplyChannelStore;
import io.github.keymaster65.copper2go.workflowapi.RequestChannelStore;
import org.copperengine.core.util.PojoDependencyInjector;

/**
 * Injector for the 3 channel stores: event, request and reply
 */
public final class Copper2goDependencyInjector extends PojoDependencyInjector {

    /**
     * Registers the channel stores.
     *
     * @param replyChannelStore to be registered
     * @param eventChannelStore to be registered
     * @param requestChannelStore to be registered
     */
    public Copper2goDependencyInjector(
            final ReplyChannelStore replyChannelStore,
            final EventChannelStore eventChannelStore,
            final RequestChannelStore requestChannelStore
    ) {
        this.register("replyChannelStore", replyChannelStore);
        this.register("eventChannelStore", eventChannelStore);
        this.register("requestChannelStore", requestChannelStore);
    }
}
