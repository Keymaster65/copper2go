package de.wolfsvl.copper2go.application;

import de.wolfsvl.copper2go.application.config.Config;
import de.wolfsvl.copper2go.testutil.Assert;
import de.wolfsvl.copper2go.testutil.Data;
import de.wolfsvl.copper2go.testutil.TestHttpClient;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.net.http.HttpResponse;

import static de.wolfsvl.copper2go.application.Application.HTTP_SERVER_PORT;
import static de.wolfsvl.copper2go.testutil.Data.getExpectedHello2Mapping;

class ApplicationHttpTest {

    @Test()
    void masterHelloTest() throws Exception {
        String name = Data.getName();
        Config config = Config.of();
        config = new Config(config.httpRequestChannelConfigs, config.workflowRepositoryConfig.withBranch( "master"), 10, HTTP_SERVER_PORT);
        Application application = Application.of(config);
        application.start();
        HttpResponse<String> response = TestHttpClient.post(URI.create("http://localhost:" + HTTP_SERVER_PORT + "/1.0/Hello"), name);
        application.stop();
        Assertions.assertThat(response.statusCode()).isEqualTo(200);
        Assert.assertResponse(response.body(), Data.getExpectedHello(name));
    }

    @Test()
    void masterHello2MappingTest() throws Exception {
        String name = Data.getName();
        Config config = Config.of();
        config = new Config(config.httpRequestChannelConfigs, config.workflowRepositoryConfig.withBranch( "master"), 10, HTTP_SERVER_PORT);
        Application application = Application.of(config);
        application.start();
        HttpResponse<String> response = TestHttpClient.post(URI.create("http://localhost:" + HTTP_SERVER_PORT + "/2.0/Hello"), name);
        application.stop();
        Assertions.assertThat(response.statusCode()).isEqualTo(200);
        Assert.assertResponse(response.body(), getExpectedHello2Mapping(name));
    }

    @Test()
    void masterHello2EmptyNameTest() throws Exception {
        String name = "";
        Config config = Config.of();
        config = new Config(config.httpRequestChannelConfigs, config.workflowRepositoryConfig.withBranch( "master"), 10, HTTP_SERVER_PORT);
        Application application = Application.of(config);
        application.start();
        HttpResponse<String> response = TestHttpClient.post(URI.create("http://localhost:" + HTTP_SERVER_PORT + "/2.0/Hello"), name);
        application.stop();
        SoftAssertions.assertSoftly(
                softAssertions -> {
                    softAssertions.assertThat(response.statusCode()).isEqualTo(500);
                    softAssertions.assertThat(response.body()).isEqualTo("A name must be specified.");
                }
        );
    }
}