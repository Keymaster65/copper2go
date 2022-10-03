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

import io.github.keymaster65.copper2go.engine.vanilla.engineapi.VanillaEngine;
import io.github.keymaster65.copper2go.engine.vanilla.workflowapi.WorkflowFactory;
import io.github.keymaster65.copper2go.engine.vanilla.workflowapi.WorkflowFactoryFactory;

public class WorkflowFactoryFactoryImpl implements WorkflowFactoryFactory {

    @Override
    public WorkflowFactory create(final VanillaEngine vanillaEngine) {
        return new WorkflowFactoryImpl(vanillaEngine);
    }
}
