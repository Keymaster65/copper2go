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
package io.github.keymaster65.copper2go.application;

import io.github.keymaster65.copper2go.application.config.Config;
import io.github.keymaster65.copper2go.connector.http.TestHttpClient;
import io.github.keymaster65.copper2go.api.connector.EngineException;
import io.github.keymaster65.copper2go.connector.http.vertx.receiver.ApiPath;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.http.HttpResponse;

class ApplicationHttpTest {

    public static final String HTTP_LOCALHOST = "http://localhost:";
    public static final String HELLO_2 = "2.0/Hello";
    public static final String HELLO_1 = "1.0/Hello?a=1";

    @Test()
    void helloTest() throws IOException, EngineException, InterruptedException {
        String name = Data.getName();
        Config config = Config.of();
        Application application = ApplicationFactory.of(config);
        HttpResponse<String> response;
        try {
            application.start();
            response = TestHttpClient.post(URI.create(HTTP_LOCALHOST + config.httpPort + ApiPath.TWOWAY_PATH + HELLO_1), name);
        } finally {
            application.stop();
        }
        Assertions.assertThat(response.statusCode()).isEqualTo(HttpURLConnection.HTTP_OK); // NOSONAR
        Assertions.assertThat(response.body()).contains(Data.getExpectedHello(name));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "configVanilla.json",
            "config.json"
    })    void hello2MappingTest(final String configName) throws IOException, EngineException, InterruptedException {
        String name = Data.getName();
        Config config = Config.ofResource("/io/github/keymaster65/copper2go/application/config/" + configName);

        Application application = ApplicationFactory.of(config);
        HttpResponse<String> response;
        try {
            application.start();
            response = TestHttpClient.post(URI.create(HTTP_LOCALHOST + config.httpPort + ApiPath.TWOWAY_PATH + HELLO_2), name);
        } finally {

            application.stop();
        }
        Assertions.assertThat(response.statusCode()).isEqualTo(HttpURLConnection.HTTP_OK);
        Assertions.assertThat(response.body()).contains(Data.getExpectedHello2Mapping(name));
    }

    @Test()
    void masterHello2EmptyNameTest() throws IOException, EngineException, InterruptedException {
        String name = "";
        Config config = Config.of();
        Application application = ApplicationFactory.of(config);
        HttpResponse<String> response;
        try {
            application.start();
            response = TestHttpClient.post(URI.create(HTTP_LOCALHOST + config.httpPort + ApiPath.TWOWAY_PATH + HELLO_2), name);
        } finally {
            application.stop();
        }
        SoftAssertions.assertSoftly(
                softAssertions -> {
                    softAssertions.assertThat(response.statusCode()).isEqualTo(HttpURLConnection.HTTP_INTERNAL_ERROR);
                    softAssertions.assertThat(response.body()).isEqualTo("IllegalArgumentException: A name must be specified.");
                }
        );
    }

    @Test()
    void masterHello2EmptyNameEventTest() throws IOException, EngineException, InterruptedException {
        String name = "";
        Config config = Config.of();
        Application application = ApplicationFactory.of(config);
        HttpResponse<String> response;
        try {
            application.start();
            response = TestHttpClient.post(URI.create(HTTP_LOCALHOST + config.httpPort + ApiPath.ONEWAY_PATH + HELLO_2), name);
        } finally {
            application.stop();
        }
        SoftAssertions.assertSoftly(
                softAssertions -> {
                    softAssertions.assertThat(response.statusCode()).isEqualTo(HttpURLConnection.HTTP_ACCEPTED);
                    softAssertions.assertThat(response.body()).isEmpty();
                }
        );
    }
}