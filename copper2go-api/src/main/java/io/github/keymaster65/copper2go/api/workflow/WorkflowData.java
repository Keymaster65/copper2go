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
package io.github.keymaster65.copper2go.api.workflow;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Holds the payload data and attributes for a workflow.
 */
public final class WorkflowData implements Serializable {

    private static final long serialVersionUID = 3L;

    /**
     * The initial payload.
     */
    private final String payload; // NOSONAR

    /**
     * Identifies the payload and can be used for replies
     */
    private final String uuid;

    /**
     * Additional attributes of the WorkflowData. Might be null.
     */
    private final Map<String,String> attributes;

    /**
     * Creates the data without attributes.
     *
     * @param uuid identifies the payload and can be used for replies.
     * @param payload workflow data
     */
    public WorkflowData(final String uuid, final String payload) {
        this(uuid, payload, null);
    }

    /**
     * Creates the data without attributes.
     *
     * @param uuid identifies the payload and can be used for replies.
     * @param payload workflow data
     * @param attributes additional attributes
     */
    public WorkflowData(final String uuid, final String payload, final Map<String,String> attributes) {
        this.uuid = uuid;
        this.payload = payload; // NOSONAR
        if (attributes != null) {
            this.attributes = new HashMap<>();
            this.attributes.putAll(attributes);
        } else {
            this.attributes = null;
        }
    }

    /**
     * The UUID, that can be used for the reply {@link ReplyChannelStore#reply(String, String)}
     * of the called workflow.
     *
     * @return UUID
     */
    public String getUUID() {
        return uuid;
    }

    /**
     * @return the initial workflow data payload
     */
    public String getPayload() {
        return payload; // NOSONAR
    }

    /**
     * Gets the attribute value.
     *
     * @param name of the attribute
     * @return the named attribute value or null.
     */
    public String getAttribute(final String name) {
        if (attributes == null) {
            return null;
        }
        return attributes.get(name);
    }

    /**
     * Gets a copy of all additional attributes.
     *
     * @return copy of all additional attributes
     */
    public Map<String,String> getAttributes() {
        if (attributes == null) {
            return Map.of();
        }
        return new HashMap<>(attributes);
    }
}
