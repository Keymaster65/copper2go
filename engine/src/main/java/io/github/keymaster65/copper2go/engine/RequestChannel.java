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

import java.util.Map;

public interface RequestChannel {
    default void request(
            final String request,
            final String responseCorrelationId
    ) {
        request(request, null, responseCorrelationId);
    }
    void request(
            final String request,
            final Map<String, String> attributes,
            final String responseCorrelationId
    );
    void close();
}
