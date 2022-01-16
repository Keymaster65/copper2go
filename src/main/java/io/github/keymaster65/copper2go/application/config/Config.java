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

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.keymaster65.copper2go.connector.http.vertx.request.HttpRequestChannelConfig;
import io.github.keymaster65.copper2go.connector.kafka.vertx.receiver.KafkaReceiverConfig;
import io.github.keymaster65.copper2go.connector.kafka.vertx.request.KafkaRequestChannelConfig;
import io.github.keymaster65.copper2go.engine.scotty.WorkflowRepositoryConfig;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Objects;

public class Config {

    public final Map<String, HttpRequestChannelConfig> httpRequestChannelConfigs;
    public final WorkflowRepositoryConfig workflowRepositoryConfig;
    public final Map<String, KafkaReceiverConfig> kafkaReceiverConfigs;
    public final int maxTickets;
    public final int httpPort;
    public final String kafkaHost;
    public final int kafkaPort;
    public final Map<String, KafkaRequestChannelConfig> kafkaRequestChannelConfigs;

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public Config(
            @JsonProperty(value = "httpRequestChannelConfigs") final Map<String, HttpRequestChannelConfig> httpRequestChannelConfigs,
            @JsonProperty(required = true, value = "workflowRepositoryConfig") final WorkflowRepositoryConfig workflowRepositoryConfig,
            @JsonProperty(value = "kafkaReceiverConfigs") final Map<String, KafkaReceiverConfig> kafkaReceiverConfigs,
            @JsonProperty(required = true, value = "maxTickets") final int maxTickets,
            @JsonProperty(required = true, value = "httpPort") final int httpPort,
            @JsonProperty(value = "kafkaHost") final String kafkaHost,
            @JsonProperty(value = "kafkaPort") final int kafkaPort,
            @JsonProperty(value = "kafkaChannelConfigs") final Map<String, KafkaRequestChannelConfig> kafkaRequestChannelConfigs
    ) {
        this.httpRequestChannelConfigs = httpRequestChannelConfigs;
        this.workflowRepositoryConfig = workflowRepositoryConfig;
        this.kafkaReceiverConfigs = kafkaReceiverConfigs;
        this.maxTickets = maxTickets;
        this.httpPort = httpPort;
        this.kafkaHost = kafkaHost;
        this.kafkaPort = kafkaPort;
        this.kafkaRequestChannelConfigs = kafkaRequestChannelConfigs;
    }

    public static Config of() throws IOException {

        return ofResource("/io/github/keymaster65/copper2go/application/config/config.json");
    }

    public static Config ofResource(final String resourcePath) throws IOException {

        return objectMapper.readValue(
                new InputStreamReader(Objects.requireNonNull(Config.class.getResourceAsStream(resourcePath)), StandardCharsets.UTF_8),
                Config.class);
    }

    public static Config of(final String config) throws IOException {
        return objectMapper.readValue(config, Config.class);
    }
}
