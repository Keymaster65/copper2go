/*
 * Copyright 2021 Wolf Sluyterman van Langeweyde
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.keymaster65.copper2go;

import io.github.keymaster65.copper2go.api.connector.EngineException;
import io.github.keymaster65.copper2go.application.Copper2GoApplicationFactory;
import io.github.keymaster65.copper2go.application.Copper2GoApplication;
import io.github.keymaster65.copper2go.application.Data;
import io.github.keymaster65.copper2go.application.config.Config;
import io.github.keymaster65.copper2go.connector.standardio.StandardInOutException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

class ApplicationApplicationTestInOutTest {

    @Test()
    void masterHelloTest() throws EngineException, IOException {
        String name = Data.getName();
        final String result = stdinHelloTest(name);
        Assertions.assertThat(result).contains(Data.getExpectedHello(name));
    }

    private String stdinHelloTest(final String name) throws IOException, EngineException {
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(byteArrayOutputStream));
        String input = name + "\r\nexit\r\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        Config config = Config.createDefault();
        Copper2GoApplication application = (Copper2GoApplication) Copper2GoApplicationFactory.create(config);
        Assertions.assertThatExceptionOfType(StandardInOutException.class).isThrownBy(application::startWithStdInOut);
        application.stop();
        return byteArrayOutputStream.toString(StandardCharsets.UTF_8).replace("\r", "").replace("\n", "");
    }
}