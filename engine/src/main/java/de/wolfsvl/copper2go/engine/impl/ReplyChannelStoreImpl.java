/*
 * Copyright 2019 Wolf Sluyterman van Langeweyde
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
package de.wolfsvl.copper2go.engine.impl;

import de.wolfsvl.copper2go.engine.ReplyChannel;
import de.wolfsvl.copper2go.workflowapi.ReplyChannelStore;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ReplyChannelStoreImpl implements ReplyChannelStore {
    private static Map<String, ReplyChannel> replyChannelMap = new ConcurrentHashMap<>();

    public void store(String id, ReplyChannel replyChannel) {
        replyChannelMap.put(id, replyChannel);
    }

    @Override
    public void reply (String uuid, String message) {
        ReplyChannel replyChannel = replyChannelMap.remove(uuid);
        replyChannel.reply(message);
    }

    @Override
    public void replyError (String id, String message) {
        ReplyChannel replyChannel = replyChannelMap.remove(id);
        replyChannel.replyError(message);
    }
    public ReplyChannel getReplyChannel(String uuid) {
        return replyChannelMap.get(uuid);
    }
}
