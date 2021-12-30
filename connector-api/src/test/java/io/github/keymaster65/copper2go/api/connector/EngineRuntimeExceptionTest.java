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
package io.github.keymaster65.copper2go.api.connector;

import net.jqwik.api.Example;
import org.assertj.core.api.Assertions;

class EngineRuntimeExceptionTest {

    public static final String MESSAGE = "message";
    public static final String MESSAGE2 = "message2";

    @Example
    void constructorMessage() {
        final EngineRuntimeException engineRuntimeException = new EngineRuntimeException(MESSAGE);

        Assertions.assertThat(engineRuntimeException.getMessage()).isEqualTo(MESSAGE);
    }

    @Example
    void constructorMessageCause() {
        final EngineException cause = new EngineException(MESSAGE2);
        final EngineRuntimeException engineRuntimeException = new EngineRuntimeException(MESSAGE, cause);

        Assertions.assertThat(engineRuntimeException.getMessage()).isEqualTo(MESSAGE);
        Assertions.assertThat(engineRuntimeException.getCause()).isSameAs(cause);
    }
}
