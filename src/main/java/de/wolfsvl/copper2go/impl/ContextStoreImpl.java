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
package de.wolfsvl.copper2go.impl;

import de.wolfsvl.copper2go.workflowapi.Context;
import de.wolfsvl.copper2go.workflowapi.ContextStore;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ContextStoreImpl implements ContextStore {
    private static Map<String, Context> contextMap = new ConcurrentHashMap<>();

    public void store(String id, Context context) {
        contextMap.put(id, context);
    }

    public void reply (String id, String message) {
        Context context = contextMap.remove(id);
        context.reply(message);
    }

    @Override
    public Context getContext(String uuid) {
        return contextMap.get(uuid);
    }
}
