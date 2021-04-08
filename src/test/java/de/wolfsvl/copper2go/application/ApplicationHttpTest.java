package de.wolfsvl.copper2go.application;

import de.wolfsvl.copper2go.config.Config;
import de.wolfsvl.copper2go.connector.standardio.StandardInOutException;
import de.wolfsvl.copper2go.testutil.Assert;
import de.wolfsvl.copper2go.testutil.Data;
import de.wolfsvl.copper2go.testutil.TestHttpClient;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.net.URI;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

import static de.wolfsvl.copper2go.application.Application.HTTP_SERVER_PORT;
import static de.wolfsvl.copper2go.testutil.Data.getExpectedPartMappingBranch;

class ApplicationHttpTest {

    @Test()
    void masterTest() throws Exception {
        String name = Data.getName();
        Config config = Config.of();
        config.getWorkflowRepositoryConfig().branch = "master";
        Application application = Application.of(config);
        application.start();
        HttpResponse<String> response = TestHttpClient.post(URI.create("http://localhost:" + HTTP_SERVER_PORT), name);
        application.stop();
        Assert.assertResponse(response.body(), Data.getExpectedPartMaster(name));
    }

    @Test()
    void mappingBranchTest() throws Exception {
        String name = Data.getName();
        Config config = Config.of();
        config.getWorkflowRepositoryConfig().branch = "feature/1.mapping";
        Application application = Application.of(config);
        application.start();
        HttpResponse<String> response = TestHttpClient.post(URI.create("http://localhost:" + HTTP_SERVER_PORT), name);
        application.stop();
        Assert.assertResponse(response.body(), getExpectedPartMappingBranch(name));
    }
}