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
package io.github.keymaster65.copper2go;

import io.github.keymaster65.copper2go.application.Assert;
import io.github.keymaster65.copper2go.application.Data;
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
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpResponse;

class SystemTest {

    private static final Logger log = LoggerFactory.getLogger(SystemTest.class);
    private static GenericContainer<?> copper2GoContainer;

    @Test
    void license() throws URISyntaxException, IOException, InterruptedException {
        HttpResponse<String> response = TestHttpClient.post(getUriBase("/"), "");
        Assertions.assertThat(response.body()).contains("Dependency License Report for copper2go");
    }

    private URI getUriBase(final String path) throws URISyntaxException {
        return new URI(String.format("http://%s:%d%s",
                copper2GoContainer.getHost(),
                copper2GoContainer.getFirstMappedPort(),
                path));
    }

    @Test
    void hello2() throws URISyntaxException, IOException, InterruptedException {
        String name = "Wolf";
        HttpResponse<String> response = TestHttpClient.post(
                getUriBase("/copper2go/2/api/request/2.0/Hello"),
                name);
        Assert.assertResponse(response.body(), Data.getExpectedHello2Mapping(name));
    }


    @Test
    void hello() throws URISyntaxException, IOException, InterruptedException {
        String name = "Wolf";
        HttpResponse<String> response = TestHttpClient.post(
                getUriBase("/copper2go/2/api/request/1.0/Hello"),
                name);
        Assert.assertResponse(response.body(), Data.getExpectedHello(name));
    }

    @Test
    void exception() throws URISyntaxException, IOException, InterruptedException {
        HttpResponse<String> response = TestHttpClient.post(
                getUriBase("/bad"),
                "name");
        Assert.assertResponse(response.body(), "Exception while getting licenses from uri /bad. null");
    }

    @Test
    void exception2() throws URISyntaxException, IOException, InterruptedException {
        HttpResponse<String> response = TestHttpClient.post(
                getUriBase("/copper2go/2/api/request/1.0/Bad"),
                "name");
        Assert.assertResponse(response.body(), "Exception: Exception while running workflow.");
    }

    @BeforeAll
    static void startContainer() {
        copper2GoContainer = new GenericContainer<>(DockerImageName.parse("keymaster65/copper2go:latest"))
                .withExposedPorts(59665);
        copper2GoContainer.start();
    }

    @AfterAll
    static void stopContainer() {
        copper2GoContainer.stop();
    }
}
