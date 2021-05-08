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
package io.github.keymaster65.copper2go.connector.http.vertx;

import io.vertx.core.MultiMap;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import static java.util.Map.entry;

class RequestHandlerTest {

    @Test
    void createAttributes() {
        MultiMap multiMap = MultiMap.caseInsensitiveMultiMap();
        multiMap.add("a", "A");
        Assertions.assertThat(RequestHandler.createAttributes(multiMap))
                .containsOnly(entry("a", "A"))
                .hasSize(1);
    }

    @Test
    void createAttributesDouble() {
        MultiMap multiMap = MultiMap.caseInsensitiveMultiMap();
        multiMap.add("a", "A");
        multiMap.add("a", "A");
        Assertions.assertThat(RequestHandler.createAttributes(multiMap))
                .containsOnly(entry("a", "A"))
                .hasSize(1);
    }

    @Test
    void createAttributesEmpty() {
        MultiMap multiMap = MultiMap.caseInsensitiveMultiMap();
        Assertions.assertThat(RequestHandler.createAttributes(multiMap)).isNull();
    }

    @Test
    void createAttributesNull() {
        Assertions.assertThat(RequestHandler.createAttributes(null)).isNull();
    }
}