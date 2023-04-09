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


import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import org.assertj.core.api.Assertions;

import java.time.Duration;

class DelayerFactoryTest {

    @Property
    void testDelay(@ForAll DelayerFactory.Mode delayMode) {
        final Delayer delayer = DelayerFactory.create(delayMode);
        final long start = System.nanoTime();
        final Duration delay = Duration.ofSeconds(1);


        delayer.delay(delay);
        final long end = System.nanoTime();


        Assertions.assertThat(end - start).isGreaterThan(delay.toNanos());
    }

}