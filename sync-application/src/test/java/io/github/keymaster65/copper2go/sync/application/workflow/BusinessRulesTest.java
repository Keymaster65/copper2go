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

import net.jqwik.api.Example;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

class BusinessRulesTest {

    @Example
    void givenOneSecond_whenCalculatePriceWith60CentPerMinute_thenCalculatePriceIsOne() {
        assertThat(
                BusinessRules.calculatePrice(
                        Duration.ofSeconds(1).toNanos(),
                        Duration.ofSeconds(2).toNanos(),
                        60
                )
        ).isEqualTo(1);
    }

    @Example
    void givenOneMinute_whenCalculatePriceWith60CentPerMinute_thenCalculatePriceIs60() {
        assertThat(
                BusinessRules.calculatePrice(
                        Duration.ofSeconds(0).toNanos(),
                        Duration.ofSeconds(60).toNanos(),
                        60
                )
        ).isEqualTo(60);
    }

}