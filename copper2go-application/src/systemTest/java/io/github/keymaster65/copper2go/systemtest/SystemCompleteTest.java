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
package io.github.keymaster65.copper2go.systemtest;

import io.github.keymaster65.copper2go.Main;
import io.github.keymaster65.copper2go.application.config.Config;
import io.github.keymaster65.copper2go.connector.http.TestHttpClient;
import org.assertj.core.api.Assertions;
import org.junit.Rule;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.utility.DockerImageName;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.concurrent.locks.LockSupport;
import java.util.stream.Collectors;

class SystemCompleteTest {

    private static final Logger log = LoggerFactory.getLogger(SystemCompleteTest.class);
    public static final String SYSTEM_PROPERTY_COPPER2GO_VERSION = "copper2go.version";
    private static GenericContainer<?> copper2GoContainer;
    static KafkaContainer kafka;

    @Rule
    public static final Network network = Network.newNetwork();

    @Test
    void systemTest() throws URISyntaxException, IOException, InterruptedException {
        String name = "name=Wolf";
        HttpResponse<String> response = TestHttpClient.post(
                Commons.getUri("/copper2go/3/api/twoway/1.0/SystemTest", copper2GoContainer),
                name);
        Assertions.assertThat(response.body()).contains(name);
    }

    @BeforeAll
    static void startContainer() throws IOException {
        log.info("Starting TestContainer.");
        startKafkaContainer();
        startCopper2GoContainer();
        log.info("TestContainer started.");
    }

    @AfterAll
    static void stopContainer() {
        log.info("Stopping TestContainer.");
        copper2GoContainer.stop();
        kafka.stop();
        log.info("TestContainer stopped.");
    }

    static void startKafkaContainer() {
        kafka = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:6.2.2")) // NOSONAR
                .withNetwork(network)
                .withNetworkAliases("kafka");
        kafka.start();
        while (!kafka.isRunning()) {
            log.info("Wait for kafka running.");
            LockSupport.parkNanos(50L * 1000 * 1000);
        }
        log.info("Kafka server: {} with port {}. Exposed: {}", kafka.getBootstrapServers(), kafka.getFirstMappedPort(), kafka.getExposedPorts());
    }

    private static void startCopper2GoContainer() throws IOException {
        try (final InputStream inputStream = Objects.requireNonNull(Config.class.getResourceAsStream("/io/github/keymaster65/copper2go/application/config/configSystemTestComplete.json"))) {
            final String config = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))
                    .lines()
                    .collect(Collectors.joining("\n"));
            copper2GoContainer = new GenericContainer<>(DockerImageName.parse("keymaster65/copper2go:%s".formatted(System.getProperty(SYSTEM_PROPERTY_COPPER2GO_VERSION)))) // NOSONAR
                    .withExposedPorts(19666)
                    .withImagePullPolicy(imageName -> true)
                    .withNetworkAliases("copper2go")
                    .withNetwork(network)
                    .withEnv(Main.ENV_C2G_CONFIG, config);
            copper2GoContainer.start();

            log.info("copper2go server started with port {}. Exposed: {}", copper2GoContainer.getFirstMappedPort(), copper2GoContainer.getExposedPorts());

        }
    }
}
