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

import io.github.keymaster65.copper2go.engine.ReplyChannelStoreImpl;
import io.github.keymaster65.copper2go.api.connector.DefaultEventChannelStore;
import io.github.keymaster65.copper2go.api.connector.DefaultRequestChannelStore;
import io.github.keymaster65.copper2go.api.connector.EngineException;
import io.github.keymaster65.copper2go.api.connector.PayloadReceiver;
import io.github.keymaster65.copper2go.api.connector.ReplyChannel;
import io.github.keymaster65.copper2go.api.workflow.WorkflowData;

import java.util.Map;
import java.util.UUID;

public class PayloadReceiverImpl implements PayloadReceiver {

    private final ReplyChannelStoreImpl replyChannelStore;

    public PayloadReceiverImpl(final ReplyChannelStoreImpl replyChannelStore) {
        this.replyChannelStore = replyChannelStore;
    }

    @Override
    public void receive(
            final String payload,
            final Map<String, String> attributes,
            final ReplyChannel replyChannel,
            final String workflow,
            final long major,
            final long minor) throws EngineException {
        String uuid = null;
        if (replyChannel != null) {
            uuid = UUID.randomUUID().toString();
            replyChannelStore.store(uuid, replyChannel);
        }

        new Hello_1(
                replyChannelStore,
                new DefaultRequestChannelStore(),
                new DefaultEventChannelStore()

        ).main(new WorkflowData(uuid, payload));

    }
}
