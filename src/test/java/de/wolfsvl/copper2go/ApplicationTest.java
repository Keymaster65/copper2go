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
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(byteArrayOutputStream));
        String name = "Wolf" + System.currentTimeMillis();
        String input = name + " \r\nexit\r\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        Application.main(new String[]{"master"});
        final String start = "Enter your name: " + "HEllo " + name + " ! (Fix the bug;-)";
        final String result = byteArrayOutputStream.toString(StandardCharsets.UTF_8).replace("\r", "").replace("\n", "");
        Assertions.assertTrue(result.length() > start.length(), "Longer result.");
        Assertions.assertTrue(result.contains(start), "Dialog contains as expected.");
    }

    @Test()
    void mappingBranchTest() throws Exception {
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(byteArrayOutputStream));
        String name = "Wolf" + System.currentTimeMillis();
        String input = name + " \r\nexit\r\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        Application.main(new String[]{"feature/1.mapping"});
        final String start = "Hello " + name + "! Please transfer";
        final String result = byteArrayOutputStream.toString(StandardCharsets.UTF_8).replace("\r", "").replace("\n", "");
        Assertions.assertTrue(result.length() > start.length(), "Longer result.");
        Assertions.assertTrue(result.contains(start), "Dialog contains as expected.");
    }
}