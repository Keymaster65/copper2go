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

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public final class WorkflowData implements Serializable {
    private static final long serialVersionUID = 2L;
    private final String uuid;

    /**
     * Additional attributes of the WorkflowData. Might be null.
     */
    private final Map<String,String> attributes;

    public WorkflowData(final String uuid, final String payload) {
        this(uuid, payload, null);
    }

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
     * Clears the payload, so that resources are not wasted when play is not needed any more.
     */
    public void clearPayload() {
        payload = null; // NOSONAR
    }
    /**
     * @param name of the attribute
     * @return the named attribute or null.
     */
    public String getAttribute(final String name) {
        if (attributes == null) {
            return null;
        }
        return attributes.get(name);
    }

    public Map<String,String> getAttributes() {
        if (attributes == null) {
            return null;
        }
        return new HashMap<>(attributes);
    }

    /**
     * Might become invisible in later major releases.
     * @deprecated Use {link {@link #getPayload()}} or {link {@link #clearPayload()}}
     */
    @SuppressWarnings("DeprecatedIsStillUsed")
    @Deprecated(since = "2.0.1")
    public String payload; // NOSONAR
}
