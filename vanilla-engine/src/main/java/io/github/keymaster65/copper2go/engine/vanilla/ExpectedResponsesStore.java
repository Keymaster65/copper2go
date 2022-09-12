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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class ExpectedResponsesStore {

    private final Map<String, Continuation> expectedResponses;
    private static final Logger log = LoggerFactory.getLogger(ExpectedResponsesStore.class);


    public ExpectedResponsesStore(
            final Map<String, Continuation> expectedResponses
    ) {
        this.expectedResponses = expectedResponses;
    }

    Continuation addExpectedResponse(final String responseCorrelationId, final Continuation continuation) {
        log.debug("Add expected response (responseCorrelationId={})", responseCorrelationId);
        return expectedResponses.put(responseCorrelationId, continuation);
    }

    Continuation removeExpectedResponse(final String responseCorrelationId) {
        log.debug("Remove expected response (responseCorrelationId={})", responseCorrelationId);
        return expectedResponses.remove(responseCorrelationId);
    }

    public long size() {
        return expectedResponses.size();
    }
}
