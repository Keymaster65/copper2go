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

import io.github.keymaster65.copper2go.connector.http.TestHttpClient;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.http.HttpResponse;

import static io.github.keymaster65.copper2go.systemtest.SystemCompleteTest.SYSTEM_PROPERTY_COPPER2GO_VERSION;

class SystemDefaultTest {

    private static final Logger log = LoggerFactory.getLogger(SystemDefaultTest.class);
    private static GenericContainer<?> copper2GoContainer;

    @Test
    void license() throws URISyntaxException, IOException, InterruptedException {
        HttpResponse<String> response = TestHttpClient.post(Commons.getUri("/", copper2GoContainer), "");
        Assertions.assertThat(response.body()).contains("Dependency License Report for");
    }

    @Test
    void hello2() throws URISyntaxException, IOException, InterruptedException {
        String name = "Wolf";
        HttpResponse<String> response = TestHttpClient.post(
                Commons.getUri("/copper2go/3/api/twoway/2.0/Hello", copper2GoContainer),
                name);
        Assertions.assertThat(response.body()).contains(Data.getExpectedHello2Mapping(name));
    }


    @Test
    void hello() throws URISyntaxException, IOException, InterruptedException {
        String name = "Wolf";
        HttpResponse<String> response = TestHttpClient.post(
                Commons.getUri("/copper2go/3/api/twoway/1.0/Hello", copper2GoContainer),
                name);
        Assertions.assertThat(response.body()).contains(Data.getExpectedHello(name));
    }

    @Test
    void exception() throws URISyntaxException, IOException, InterruptedException {
        HttpResponse<String> response = TestHttpClient.post(
                Commons.getUri("/bad", copper2GoContainer),
                "name");
        Assertions.assertThat(response.body()).contains("Exception while getting licenses from uri /bad. null");
    }

    @Test
    void exception2() throws URISyntaxException, IOException, InterruptedException {
        HttpResponse<String> response = TestHttpClient.post(
                Commons.getUri("/copper2go/3/api/twoway/1.0/Bad", copper2GoContainer),
                "name");
        Assertions.assertThat(response.body()).contains("Exception: Exception while running workflow.");
    }

    @BeforeAll
    static void startContainer() {
        //noinspection resource is in stopContainer
        copper2GoContainer = new GenericContainer<>(DockerImageName.parse("keymaster65/copper2go:%s".formatted(System.getProperty(SYSTEM_PROPERTY_COPPER2GO_VERSION))))
                .withExposedPorts(59665)
                .withImagePullPolicy(imageName -> true);
        copper2GoContainer.start();
        log.info("TestContainer started.");
    }

    @AfterAll
    static void stopContainer() {
        copper2GoContainer.stop();
    }

}
