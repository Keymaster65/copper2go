package de.wolfsvl.copper2go.application;

import de.wolfsvl.copper2go.application.config.Config;
import de.wolfsvl.copper2go.connector.http.TestHttpClient;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;

import java.net.HttpURLConnection;
import java.net.URI;
import java.net.http.HttpResponse;

import static de.wolfsvl.copper2go.application.Data.getExpectedHello2Mapping;

class ApplicationHttpTest {

    @Test()
    void masterHelloTest() throws Exception {
        String name = Data.getName();
        Config config = Config.of();
        config = new Config(config.httpRequestChannelConfigs, config.workflowRepositoryConfig.withBranch( "master"), 10, config.httpPort);
        Application application = Application.of(config);
        application.start();
        HttpResponse<String> response = TestHttpClient.post(URI.create("http://localhost:" + config.httpPort + "/1.0/Hello"), name);
        application.stop();
        Assertions.assertThat(response.statusCode()).isEqualTo(HttpURLConnection.HTTP_OK);
        Assert.assertResponse(response.body(), Data.getExpectedHello(name));
    }

    @Test()
    void masterHello2MappingTest() throws Exception {
        String name = Data.getName();
        Config config = Config.of();
        config = new Config(config.httpRequestChannelConfigs, config.workflowRepositoryConfig.withBranch( "master"), 10, config.httpPort);
        Application application = Application.of(config);
        application.start();
        HttpResponse<String> response = TestHttpClient.post(URI.create("http://localhost:" + config.httpPort + "/2.0/Hello"), name);
        application.stop();
        Assertions.assertThat(response.statusCode()).isEqualTo(HttpURLConnection.HTTP_OK);
        Assert.assertResponse(response.body(), getExpectedHello2Mapping(name));
    }

    @Test()
    void masterHello2EmptyNameTest() throws Exception {
        String name = "";
        Config config = Config.of();
        config = new Config(config.httpRequestChannelConfigs, config.workflowRepositoryConfig.withBranch( "master"), 10, config.httpPort);
        Application application = Application.of(config);
        application.start();
        HttpResponse<String> response = TestHttpClient.post(URI.create("http://localhost:" + config.httpPort + "/2.0/Hello"), name);
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
        config = new Config(config.httpRequestChannelConfigs, config.workflowRepositoryConfig.withBranch( "master"), 10, config.httpPort);
        Application application = Application.of(config);
        application.start();
        HttpResponse<String> response = TestHttpClient.post(URI.create("http://localhost:" + config.httpPort + "/event/2.0/Hello"), name);
        application.stop();
        SoftAssertions.assertSoftly(
                softAssertions -> {
                    softAssertions.assertThat(response.statusCode()).isEqualTo(HttpURLConnection.HTTP_ACCEPTED);
                    softAssertions.assertThat(response.body()).isEmpty();
                }
        );
    }
}