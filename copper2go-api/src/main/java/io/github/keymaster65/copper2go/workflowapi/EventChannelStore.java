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
 * Store for defined events channels, that can be used by name in workflows to submit events.
 */
public interface EventChannelStore {

    /**
     * Submit event without attributes.
     *
     * @param channelName defined channel name
     * @param event event payload
     */
    default void event(
            final String channelName,
                    final String event
    ) {
        event(channelName, event, null);
    }

    /**
     * Submit event with attributes.
     *
     * @param channelName defined channel name
     * @param event event payload
     * @param attributes additional attributes
     */
    void event(
            final String channelName,
            final String event,
            final Map<String, String> attributes
    );

    /**
     * Submit error event without attributes.
     *
     * @param channelName defined channel name
     * @param event event payload
     */
    default void errorEvent(
            final String channelName,
                    final String event
    ) {
                errorEvent(channelName, event, null);
    }

    /**
     * Submit error event with attributes.
     *
     * @param channelName defined channel name
     * @param event event payload
     * @param attributes additional attributes
     */
    void errorEvent(
            final String channelName,
            final String event,
            final Map<String, String> attributes
    );
}