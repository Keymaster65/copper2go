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
import org.assertj.core.api.Assertions;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;

import java.net.HttpURLConnection;
import java.net.URI;
import java.net.http.HttpResponse;

import static io.github.keymaster65.copper2go.connector.http.vertx.VertxHttpServer.COPPER2GO_2_API;

class ApplicationHttpTest {

    @Test()
    void masterHelloTest() throws Exception {
        String name = Data.getName();
        Config config = Config.of();
        config = new Config(config.httpRequestChannelConfigs, config.workflowRepositoryConfig.withBranch( "release/2"), 10, config.httpPort);
        Application application = Application.of(config);
        application.start();
        HttpResponse<String> response = TestHttpClient.post(URI.create("http://localhost:" + config.httpPort + COPPER2GO_2_API + "request/1.0/Hello?a=1"), name);
        application.stop();
        Assertions.assertThat(response.statusCode()).isEqualTo(HttpURLConnection.HTTP_OK);
        Assert.assertResponse(response.body(), Data.getExpectedHello(name));
    }

    @Test()
    void masterHello2MappingTest() throws Exception {
        String name = Data.getName();
        Config config = Config.of();
        config = new Config(config.httpRequestChannelConfigs, config.workflowRepositoryConfig.withBranch( "release/2"), 10, config.httpPort);
        Application application = Application.of(config);
        application.start();
        HttpResponse<String> response = TestHttpClient.post(URI.create("http://localhost:" + config.httpPort + COPPER2GO_2_API + "request/2.0/Hello"), name);
        application.stop();
        Assertions.assertThat(response.statusCode()).isEqualTo(HttpURLConnection.HTTP_OK);
        Assert.assertResponse(response.body(), Data.getExpectedHello2Mapping(name));
    }

    @Test()
    void masterHello2EmptyNameTest() throws Exception {
        String name = "";
        Config config = Config.of();
        config = new Config(config.httpRequestChannelConfigs, config.workflowRepositoryConfig.withBranch( "release/2"), 10, config.httpPort);
        Application application = Application.of(config);
        application.start();
        HttpResponse<String> response = TestHttpClient.post(URI.create("http://localhost:" + config.httpPort + COPPER2GO_2_API + "request/2.0/Hello"), name);
        application.stop();
        SoftAssertions.assertSoftly(
                softAssertions -> {
                    softAssertions.assertThat(response.statusCode()).isEqualTo(HttpURLConnection.HTTP_INTERNAL_ERROR);
                    softAssertions.assertThat(response.body()).isEqualTo("IllegalArgumentException: A name must be specified.");
                }
        );
    }

    @Test()
    void masterHello2EmptyNameEventTest() throws Exception {
        String name = "";
        Config config = Config.of();
        config = new Config(config.httpRequestChannelConfigs, config.workflowRepositoryConfig.withBranch( "release/2"), 10, config.httpPort);
        Application application = Application.of(config);
        application.start();
        HttpResponse<String> response = TestHttpClient.post(URI.create("http://localhost:" + config.httpPort + COPPER2GO_2_API + "event/2.0/Hello"), name);
        application.stop();
        SoftAssertions.assertSoftly(
                softAssertions -> {
                    softAssertions.assertThat(response.statusCode()).isEqualTo(HttpURLConnection.HTTP_ACCEPTED);
                    softAssertions.assertThat(response.body()).isEmpty();
                }
        );
    }
}