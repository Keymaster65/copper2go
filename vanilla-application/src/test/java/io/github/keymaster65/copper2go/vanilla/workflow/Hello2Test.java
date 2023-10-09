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
package io.github.keymaster65.copper2go.vanilla.workflow;

import io.github.keymaster65.copper2go.api.workflow.WorkflowData;
import io.github.keymaster65.copper2go.engine.vanilla.engineapi.VanillaEngine;
import net.jqwik.api.Example;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.constraints.WithNull;
import org.assertj.core.api.Assertions;
import org.mockito.Mockito;

class Hello2Test {

    public static final String PAYLOAD = "payload";
    public static final String UUID = "uuid";

    @Property(tries = 100)
    void mainOnewayOrTwoway(@ForAll @WithNull final String uuid) {
        final VanillaEngine engine = Mockito.mock(VanillaEngine.class);
        final Hello2 hello2 = new Hello2(engine);
        final String responseCorrelationId = "responseCorrelationId";
        final String channelName = "Pricing.centPerMinute";
        final String request = "request";
        Mockito.when(engine.request(channelName, request)).thenReturn(responseCorrelationId);

        hello2.main(new WorkflowData(uuid, PAYLOAD));

        Mockito.verify(engine).request(channelName, request);
        Mockito.verify(engine).continueAsync(Mockito.eq(responseCorrelationId), Mockito.any());
        Mockito.verifyNoMoreInteractions(engine);
    }

    @Example
    void continuation() {
        final VanillaEngine engine = Mockito.mock(VanillaEngine.class);
        final Hello2 hello2 = new Hello2(engine, new WorkflowData(UUID, PAYLOAD));

        final String response = "response";
        hello2.continuation(response);

        Mockito.verify(engine).reply(UUID, "Hello "+ PAYLOAD + "! Please transfer "+ response);
        Mockito.verifyNoMoreInteractions(engine);
    }

    @Example
    void continuationNullUuid() {
        final VanillaEngine engine = Mockito.mock(VanillaEngine.class);
        final Hello2 hello2 = new Hello2(engine, new WorkflowData(null, PAYLOAD));

        final String response = "response";
        hello2.continuation(response);

        Mockito.verifyNoInteractions(engine);
    }

    @Example
    void continuationBadState() {
        final VanillaEngine engine = Mockito.mock(VanillaEngine.class);
        final Hello2 hello2 = new Hello2(engine);

        Assertions.assertThatCode(() -> hello2.continuation("response"))
                .isInstanceOf(NullPointerException.class);

    }
}