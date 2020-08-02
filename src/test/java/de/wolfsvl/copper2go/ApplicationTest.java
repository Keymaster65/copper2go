package de.wolfsvl.copper2go;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

class ApplicationTest {

    @Test()
    void mainTest() throws Exception {
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(byteArrayOutputStream));
        System.setIn(new ByteArrayInputStream("Wolf\r\nexit\r\n".getBytes()));
        Application.main(null);
        final String start = "Enter your name: \n" + "HEllo Wolf! (Fix the bug;-)";
        final String result = byteArrayOutputStream.toString(StandardCharsets.UTF_8).replace("\r", "");
        Assertions.assertTrue(result.length() > start.length(), "Longer result.");
        Assertions.assertEquals(start, result.substring(0,start.length()), "Dialog starts as expected.");
    }
}