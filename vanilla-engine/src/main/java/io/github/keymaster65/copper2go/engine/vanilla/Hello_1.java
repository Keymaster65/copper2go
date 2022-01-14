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

import io.github.keymaster65.copper2go.api.workflow.EventChannelStore;
import io.github.keymaster65.copper2go.api.workflow.ReplyChannelStore;
import io.github.keymaster65.copper2go.api.workflow.RequestChannelStore;
import io.github.keymaster65.copper2go.api.workflow.WorkflowData;

public class Hello_1 {

    private final ReplyChannelStore replyChannelStore;
    private final RequestChannelStore requestChannelStore;
    private final EventChannelStore eventChannelStore;

    public Hello_1(
            final ReplyChannelStore replyChannelStore,
            final RequestChannelStore requestChannelStore,
            final EventChannelStore eventChannelStore
    ) {

        this.replyChannelStore = replyChannelStore;
        this.requestChannelStore = requestChannelStore;
        this.eventChannelStore = eventChannelStore;
    }

    public void main(final WorkflowData workflowData) {
        final String uuid = workflowData.getUUID();
        if (uuid != null) {
            replyChannelStore.reply(uuid, "Hello " + workflowData.getPayload());
        }
    }
}
