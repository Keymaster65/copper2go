package de.wolfsvl.copper2go.application;

import de.wolfsvl.copper2go.connector.standardio.StandardInOutException;
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

class ApplicationTest {

    @Test()
    void masterStdTest() throws Exception {
        String name = "Wolf" + System.currentTimeMillis();
        final String start = "HEllo " + name + " ! (Fix the bug;-)";
        final String result = stdinTest(name, "master");
        assertResponse(result, start);
    }

    @Test()
    void mappingBranchStdTest() throws Exception {
        String name = "Wolf" + System.currentTimeMillis();
        final String result = stdinTest(name, "feature/1.mapping");
        final String start = "Hello " + name + "! Please transfer";
        assertResponse(result, start);
    }

    @Test()
    void masterHttpTest() throws Exception {
        String name = "Wolf" + System.currentTimeMillis();
        final String start = "HEllo " + name + "! (Fix the bug;-)";
        Application application = new Application(new String[]{"master"});
        application.start();
        HttpResponse<String> response = TestHttpClient.post(URI.create("http://localhost:" + HTTP_SERVER_PORT), name);
        application.stop();
        assertResponse(start, response.body());
    }

    private void assertResponse(final String response, final String start) {
        Assertions.assertThat(response).contains(start);
    }

    private String stdinTest(final String name, final String branch) throws Exception {
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(byteArrayOutputStream));
        String input = name + " \r\nexit\r\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        Application application = new Application(new String[]{branch});
        org.assertj.core.api.Assertions.assertThatExceptionOfType(StandardInOutException.class).isThrownBy(application::startWithStdInOut);
        application.stop();
        return byteArrayOutputStream.toString(StandardCharsets.UTF_8).replace("\r", "").replace("\n", "");
    }
}