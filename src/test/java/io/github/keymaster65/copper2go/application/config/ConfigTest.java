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
package io.github.keymaster65.copper2go.application.config;

import io.github.keymaster65.copper2go.connector.http.HttpMethod;
import io.github.keymaster65.copper2go.connector.http.HttpRequestChannelConfig;
import io.github.keymaster65.copper2go.connector.kafka.vertx.KafkaRequestChannelConfig;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

class ConfigTest {

    @Test
    void of() throws IOException {
        Config config = Config.of();
        assertThat(config.httpPort).isEqualTo(59665);
        assertThat(config.kafkaHost).isEqualTo("localhost");
        assertThat(config.kafkaPort).isEqualTo(9092);
        assertThat(config.maxTickets).isEqualTo(10000);

        assertThat(config.httpRequestChannelConfigs).hasSize(1);
        HttpRequestChannelConfig httpRequestChannelConfig = config.httpRequestChannelConfigs.get("Pricing.centPerMinute");
        assertThat(httpRequestChannelConfig.method).isEqualTo(HttpMethod.GET);
        assertThat(httpRequestChannelConfig.host).isEqualTo("localhost");
        assertThat(httpRequestChannelConfig.port).isEqualTo(59665);
        assertThat(httpRequestChannelConfig.path).isEqualTo("/copper2go/2/api/request/1.0/Pricing");

        assertThat(config.httpRequestChannelConfigs).hasSize(1);
        KafkaRequestChannelConfig requestRequestChannelConfig = config.kafkaRequestChannelConfigs.get("Hello");
        assertThat(requestRequestChannelConfig.topic).isEqualTo("testHello");
    }
}