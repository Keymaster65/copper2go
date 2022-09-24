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
import io.github.keymaster65.copper2go.connector.http.vertx.request.HttpRequestChannelConfig;
import io.github.keymaster65.copper2go.connector.kafka.vertx.receiver.KafkaReceiverConfig;
import io.github.keymaster65.copper2go.connector.kafka.vertx.request.KafkaRequestChannelConfig;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

class ConfigTest {

    @Test
    void createDefault() throws IOException {
        Config config = Config.createDefault();//ofResource("/io/github/keymaster65/copper2go/application/config/config.json");
        assertThat(config.httpPort).isEqualTo(59665);
        assertThat(config.maxTickets).isEqualTo(10000);

        assertThat(config.httpRequestChannelConfigs).hasSize(1);
        HttpRequestChannelConfig httpRequestChannelConfig = config.httpRequestChannelConfigs.get("Pricing.centPerMinute");
        assertThat(httpRequestChannelConfig.method).isEqualTo(HttpMethod.GET);
        assertThat(httpRequestChannelConfig.host).isEqualTo("localhost");
        assertThat(httpRequestChannelConfig.port).isEqualTo(59665);
        assertThat(httpRequestChannelConfig.path).isEqualTo("/copper2go/3/api/twoway/1.0/Pricing");

        assertThat(config.kafkaRequestChannelConfigs).isNull();
    }

    @Test
    void createFromResource() throws IOException {
        Config config = Config.createFromResource("/io/github/keymaster65/copper2go/application/config/configSystemTestComplete.json");
        assertThat(config.httpPort).isEqualTo(59665);
        assertThat(config.kafkaHost).isEqualTo("kafka");
        assertThat(config.kafkaPort).isEqualTo(9092);
        assertThat(config.maxTickets).isEqualTo(10000);

        assertThat(config.httpRequestChannelConfigs).hasSize(1);
        HttpRequestChannelConfig httpRequestChannelConfig = config.httpRequestChannelConfigs.get("Pricing.centPerMinute");
        assertThat(httpRequestChannelConfig.method).isEqualTo(HttpMethod.GET);
        assertThat(httpRequestChannelConfig.host).isEqualTo("copper2go");
        assertThat(httpRequestChannelConfig.port).isEqualTo(59665);
        assertThat(httpRequestChannelConfig.path).isEqualTo("/copper2go/3/api/twoway/1.0/Pricing");

        assertKafka(config);
    }

    private void assertKafka(final Config config) {
        assertThat(config.kafkaRequestChannelConfigs).hasSize(1);
        KafkaRequestChannelConfig requestRequestChannelConfig = config.kafkaRequestChannelConfigs.get("SystemTestRequestChannel");
        assertThat(requestRequestChannelConfig.topic).isEqualTo("systemTestTopic");

        assertThat(config.kafkaReceiverConfigs).hasSize(4);
        KafkaReceiverConfig kafkaReceiverManagerConfig = config.kafkaReceiverConfigs.get("Manager");
        assertThat(kafkaReceiverManagerConfig.topic).isEqualTo("test");
        assertThat(kafkaReceiverManagerConfig.groupId).isEqualTo("managerGroup");
        assertThat(kafkaReceiverManagerConfig.workflowName).isEqualTo("HelloChoreo");
        assertThat(kafkaReceiverManagerConfig.majorVersion).isOne();
        assertThat(kafkaReceiverManagerConfig.minorVersion).isZero();

        KafkaReceiverConfig kafkaReceiverCRMConfig = config.kafkaReceiverConfigs.get("CRM");
        assertThat(kafkaReceiverCRMConfig.topic).isEqualTo("test");
        assertThat(kafkaReceiverCRMConfig.groupId).isEqualTo("CRMGroup");
        assertThat(kafkaReceiverCRMConfig.workflowName).isEqualTo("CRM");
        assertThat(kafkaReceiverCRMConfig.majorVersion).isOne();
        assertThat(kafkaReceiverCRMConfig.minorVersion).isZero();

        KafkaReceiverConfig kafkaReceiverCRMConfig2 = config.kafkaReceiverConfigs.get("SystemTestReceiver");
        assertThat(kafkaReceiverCRMConfig2.topic).isEqualTo("systemTestTopic");
        assertThat(kafkaReceiverCRMConfig2.groupId).isEqualTo("systemTestGroup");
        assertThat(kafkaReceiverCRMConfig2.workflowName).isEqualTo("SystemTest");
        assertThat(kafkaReceiverCRMConfig2.majorVersion).isOne();
        assertThat(kafkaReceiverCRMConfig2.minorVersion).isZero();
    }
}