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

import io.github.keymaster65.copper2go.api.connector.EngineException;
import org.copperengine.core.DependencyInjector;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class EngineControlTest {

    @Test
    void close() throws EngineException {
        final EngineControl mockedEngineControl = Mockito.mock(EngineControl.class);
        final EngineControl engineControl = createEngineControl(mockedEngineControl);

        engineControl.close();

        Mockito.verify(mockedEngineControl).stop();
    }

    private EngineControl createEngineControl(final EngineControl wrappedEngineControl) {
        return new EngineControl() {

            @Override
            public void start(final DependencyInjector dependencyInjector) throws EngineException {
                wrappedEngineControl.start(dependencyInjector);
            }

            @Override
            public void stop() throws EngineException {
                wrappedEngineControl.stop();
            }
        };
    }
}