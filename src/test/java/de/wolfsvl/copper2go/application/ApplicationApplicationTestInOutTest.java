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

class ApplicationApplicationTestInOutTest {

    @Test()
    void masterTest() throws Exception {
        String name = Data.getName();
        final String result = stdinTest(name, "master");
        Assert.assertResponse(result, Data.getExpectedPartMaster(name));
    }

    @Test()
    void mappingBranchTest() throws Exception {
        String name = Data.getName();
        final String result = stdinTest(name, "feature/1.mapping");
        Assert.assertResponse(result, getExpectedPartMappingBranch(name));
    }

    private String stdinTest(final String name, final String branch) throws Exception {
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(byteArrayOutputStream));
        String input = name + "\r\nexit\r\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        Config config = Config.of();
        config.workflowRepositoryConfig.branch = branch;
        Application application = Application.of(config);
        Assertions.assertThatExceptionOfType(StandardInOutException.class).isThrownBy(application::startWithStdInOut);
        application.stop();
        return byteArrayOutputStream.toString(StandardCharsets.UTF_8).replace("\r", "").replace("\n", "");
    }
}