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
package io.github.keymaster65.copper2go.sync.application.workflow;

import io.github.keymaster65.copper2go.engine.sync.engineapi.SyncEngine;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import org.assertj.core.api.Assertions;
import org.mockito.Mockito;

class Pricing1Test {

    @Property
    void main(@ForAll final String payload) {
        final SyncEngine engine = Mockito.mock(SyncEngine.class);
        final Pricing1 pricing1 = new Pricing1(engine);
        final WorkflowDataImpl workflowData = new WorkflowDataImpl(payload);

        final String result = pricing1.main(workflowData);

        Assertions.assertThat(result).isEqualTo("%d cent.".formatted(payload.length()));
    }
}