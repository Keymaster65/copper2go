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
package io.github.keymaster65.copper2go.connector.http;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.ConnectException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.channels.UnresolvedAddressException;

class TestHttpClientTest {

    @Test
    void post() throws URISyntaxException {
        final URI uri = new URI("http://xxx");
        Assertions.assertThatCode(() ->
                        TestHttpClient.post(uri, "payload")
                )
                .isInstanceOf(IOException.class)
                .hasMessage("Could not send to URI http://xxx")
                .hasRootCauseInstanceOf(UnresolvedAddressException.class);
    }
}