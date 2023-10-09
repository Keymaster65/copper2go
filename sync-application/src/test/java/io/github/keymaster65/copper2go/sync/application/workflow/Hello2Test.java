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

import io.github.keymaster65.copper2go.engine.sync.engineapi.EngineException;
import io.github.keymaster65.copper2go.engine.sync.engineapi.SyncEngine;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.constraints.NotEmpty;
import org.assertj.core.api.Assertions;
import org.mockito.Mockito;

class Hello2Test {

    @SuppressWarnings("ConfusingMainMethod")
    @Property(tries = 10)
    void main(@ForAll @NotEmpty final String payload) throws EngineException {
        final SyncEngine engine = Mockito.mock(SyncEngine.class);
        final Pricing1 pricing1 = new Pricing1(engine);
        Mockito
                .when(engine.request(Mockito.eq(""), Mockito.any()))
                .thenReturn(pricing1.main(new WorkflowDataImpl(payload)));
        final Hello2 hello2 = new Hello2(engine);
        final WorkflowDataImpl workflowData = new WorkflowDataImpl(payload);


        final String result = hello2.main(workflowData);


        Assertions
                .assertThat(result)
                .startsWith("Hello %s! Please transfer ".formatted(payload));
    }

    @Property(tries = 10)
    void mainException(@ForAll @NotEmpty final String payload) throws EngineException {
        final SyncEngine engine = Mockito.mock(SyncEngine.class);
        Mockito
                .when(engine.request(Mockito.eq(""), Mockito.any()))
                .thenThrow(new EngineException("Test EngineException", new RuntimeException("Test RuntimeException")));
        final Hello2 hello2 = new Hello2(engine);
        final WorkflowDataImpl workflowData = new WorkflowDataImpl(payload);


        final String result = hello2.main(workflowData);


        Assertions
                .assertThat(result)
                .isEqualTo("EngineException: Test EngineException");
    }
}