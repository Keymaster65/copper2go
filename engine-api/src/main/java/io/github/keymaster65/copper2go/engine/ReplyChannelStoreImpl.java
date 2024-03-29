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

import io.github.keymaster65.copper2go.api.connector.ReplyChannel;
import io.github.keymaster65.copper2go.api.workflow.ReplyChannelStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class ReplyChannelStoreImpl implements ReplyChannelStore {
    private final Map<String, ReplyChannel> replyChannelMap = new ConcurrentHashMap<>();
    private static final Logger log = LoggerFactory.getLogger(ReplyChannelStoreImpl.class);

    public void store(
            final String uuid,
            final ReplyChannel replyChannel
    ) {
        Objects.requireNonNull(uuid, "uuid of ReplyChannel must not be null");

        var usedReplyChannel = Objects.requireNonNullElseGet(replyChannel, () -> new ReplyChannel() {
            @Override
            public void reply(final String message, final Map<String, String> attributes) {
                log.error("Reply channel for uuid {} not defined.", uuid);
            }

            @Override
            public void replyError(final String message, final Map<String, String> attributes) {
                log.error("Reply channel for uuid {} not defined.", uuid);
            }
        });
        replyChannelMap.put(uuid, usedReplyChannel);
    }

    @Override
    public void reply(
            final String uuid,
            final String message,
            final Map<String, String> attributes
    ) {
        var replyChannel = replyChannelMap.remove(uuid);
        replyChannel.reply(message, attributes);
    }

    @Override
    public void replyError(
            final String uuid,
            final String message,
            final Map<String, String> attributes
    ) {
        var replyChannel = replyChannelMap.remove(uuid);
        replyChannel.replyError(message, attributes);
    }

    public ReplyChannel getReplyChannel(String uuid) {
        return replyChannelMap.get(uuid);
    }
}
