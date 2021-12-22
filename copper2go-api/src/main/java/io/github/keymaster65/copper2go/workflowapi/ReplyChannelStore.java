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
package io.github.keymaster65.copper2go.workflowapi;

import java.util.Map;

/**
 * Store for replies, that can be used by name in workflows to submit replies to requests.
 */
public interface ReplyChannelStore {

    /**
     * Submit reply without attributes.
     *
     * @param uuid identifies the reply for a request
     * @param message reply payload
     */
    default void reply(
            final String uuid,
            final String message
    ) {
        reply(uuid, message, null);
    }

    /**
     * Submit reply with attributes.
     *
     * @param uuid identifies the reply for a request
     * @param message reply payload
     * @param attributes additional attributes
     */
    void reply(
            final String uuid,
            final String message,
            final Map<String,String> attributes
    );

    /**
     * Submit error reply without attributes.
     *
     * @param uuid identifies the reply for a request
     * @param message reply payload
     */
    default void replyError(
            final String uuid,
            final String message
    ) {
        replyError(uuid, message, null);
    }

    /**
     * Submit error reply without attributes.
     *
     * @param uuid identifies the reply for a request
     * @param message reply payload
     * @param attributes additional attributes
     */
    void replyError(
            final String uuid,
            final String message,
            final Map<String,String> attributes
    );
}