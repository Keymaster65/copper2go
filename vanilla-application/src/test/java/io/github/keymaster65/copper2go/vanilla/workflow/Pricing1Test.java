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
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class Pricing1Test {

    public static final String UUID = "uuid";
    public static final String PAYLOAD = "payload";

    @Test
    void main() {
        final VanillaEngine engine = Mockito.mock(VanillaEngine.class);
        final Pricing1 pricing1 = new Pricing1(engine);

        pricing1.main(new WorkflowData(UUID, PAYLOAD));

        Mockito.verify(engine).reply(UUID, "5 cent.");
    }

    @Test
    void mainOneway() {
        final VanillaEngine engine = Mockito.mock(VanillaEngine.class);
        final Pricing1 pricing1 = new Pricing1(engine);

        pricing1.main(new WorkflowData(null, PAYLOAD));

        Mockito.verifyNoInteractions(engine);
    }
}