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
package io.github.keymaster65.copper2go.connector.kafka.vertx;

import io.vertx.kafka.client.producer.KafkaHeader;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class Copper2GoKafkaSenderImplTest {

    @Test
    void createHeader() {
        Map<String,String> attributes = new HashMap<>();
        attributes.put("a", "A");
        final List<KafkaHeader> header = Copper2GoKafkaSenderImpl.createHeader(attributes);
        Assertions.assertThat(header).hasSize(1);
        Assertions.assertThat(header.get(0).key()).isEqualTo("a");
        Assertions.assertThat(header.get(0).value().toString(StandardCharsets.UTF_8)).isEqualTo("A");
    }

    @Test
    void createHeaderEmpty() {
        Map<String,String> attributes = new HashMap<>();
        Assertions.assertThat(Copper2GoKafkaSenderImpl.createHeader(attributes)).isEmpty();
    }

    @Test
    void createHeaderNull() {
        Assertions.assertThat(Copper2GoKafkaSenderImpl.createHeader(null)).isEmpty();
    }
}