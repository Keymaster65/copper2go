package de.wolfsvl.copper2go;

import de.wolfsvl.copper2go.application.Application;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

class ApplicationTest {

    @Test()
    void masterTest() throws Exception {
        String name = "Wolf" + System.currentTimeMillis();
        final String start = "Enter your name: " + "HEllo " + name + " ! (Fix the bug;-)";
        final String result = stdinTest(name, "master");
        Assertions.assertTrue(result.contains(start), "Dialog contains as expected.");
    }

    @Test()
    void mappingBranchTest() throws Exception {
        String name = "Wolf" + System.currentTimeMillis();
        final String result = stdinTest(name, "feature/1.mapping");
        final String start = "Hello " + name + "! Please transfer";
        Assertions.assertTrue(result.contains(start), "Dialog contains as expected.");
    }

    private String stdinTest(final String name, final String branch) throws Exception {
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(byteArrayOutputStream));
        String input = name + " \r\nexit\r\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        Application.main(new String[]{branch});
        return byteArrayOutputStream.toString(StandardCharsets.UTF_8).replace("\r", "").replace("\n", "");
    }
}