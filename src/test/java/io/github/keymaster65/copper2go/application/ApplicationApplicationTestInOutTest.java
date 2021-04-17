package io.github.keymaster65.copper2go.application;

import io.github.keymaster65.copper2go.application.config.Config;
import io.github.keymaster65.copper2go.connector.standardio.StandardInOutException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

class ApplicationApplicationTestInOutTest {

    @Test()
    void masterHelloTest() throws Exception {
        String name = Data.getName();
        final String result = stdinHelloTest(name, "release/2");
        Assert.assertResponse(result, Data.getExpectedHello(name));
    }

    private String stdinHelloTest(final String name, final String branch) throws Exception {
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(byteArrayOutputStream));
        String input = name + "\r\nexit\r\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        Config config = Config.of();
        config = new Config(config.httpRequestChannelConfigs, config.workflowRepositoryConfig.withBranch(branch), 10, config.httpPort);
        Application application = Application.of(config);
        Assertions.assertThatExceptionOfType(StandardInOutException.class).isThrownBy(application::startWithStdInOut);
        application.stop();
        return byteArrayOutputStream.toString(StandardCharsets.UTF_8).replace("\r", "").replace("\n", "");
    }
}