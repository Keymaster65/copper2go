package de.wolfsvl.copper2go.application;

import de.wolfsvl.copper2go.application.config.Config;
import de.wolfsvl.copper2go.testutil.Assert;
import de.wolfsvl.copper2go.testutil.Data;
import de.wolfsvl.copper2go.testutil.TestHttpClient;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.net.http.HttpResponse;

import static de.wolfsvl.copper2go.application.Application.HTTP_SERVER_PORT;
import static de.wolfsvl.copper2go.testutil.Data.getExpectedPartMappingBranch;

class ApplicationHttpTest {

    @Test()
    void masterTest() throws Exception {
        String name = Data.getName();
        Config config = Config.of();
        config = new Config(config.httpRequestChannelConfigs, config.workflowRepositoryConfig.withBranch( "master"), 10);
        Application application = Application.of(config);
        application.start();
        HttpResponse<String> response = TestHttpClient.post(URI.create("http://localhost:" + HTTP_SERVER_PORT + "/1.0/Hello"), name);
        application.stop();
        Assert.assertResponse(response.body(), Data.getExpectedPartMaster(name));
    }

    @Test()
    void mappingBranchTest() throws Exception {
        String name = Data.getName();
        Config config = Config.of();
        config = new Config(config.httpRequestChannelConfigs, config.workflowRepositoryConfig.withBranch( "feature/1.mapping"), 10);
        Application application = Application.of(config);
        application.start();
        HttpResponse<String> response = TestHttpClient.post(URI.create("http://localhost:" + HTTP_SERVER_PORT + "/1.0/Hello"), name);
        application.stop();
        Assert.assertResponse(response.body(), getExpectedPartMappingBranch(name));
    }
}