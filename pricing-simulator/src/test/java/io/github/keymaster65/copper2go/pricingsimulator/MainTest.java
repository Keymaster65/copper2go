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
package io.github.keymaster65.copper2go.pricingsimulator;

import net.jqwik.api.Example;
import org.assertj.core.api.Assertions;

class MainTest {


    @Example
    void givenHelpWanted_whenServiceCalled_thenNoException() {
        Assertions
                .assertThatCode(() -> Main.main(new String[]{"-h"}))
                .doesNotThrowAnyException();

    }

    @Example
    void givenNoArgs_whenServiceCalled_thenNoException() {
        Assertions
                .assertThatCode(() -> Main.main(new String[]{}))
                .doesNotThrowAnyException();

    }

    @SuppressWarnings("InstantiationOfUtilityClass")
    @Example
    void whenMainNewCalled_thenNoException() {
        final Main main = new Main();
        Assertions
                .assertThat(main)
                .isNotNull();
    }
}