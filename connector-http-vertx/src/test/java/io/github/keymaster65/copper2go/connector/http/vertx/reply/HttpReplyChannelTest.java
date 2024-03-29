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
package io.github.keymaster65.copper2go.connector.http.vertx.reply;

import io.vertx.core.http.HttpServerResponse;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.net.HttpURLConnection;
import java.util.Map;

class HttpReplyChannelTest {

    public static final String REPLY = "reply";

    @Test
    void reply() {
        final HttpServerResponse response = Mockito.mock(HttpServerResponse.class);
        final HttpReplyChannel httpReplyChannel = new HttpReplyChannel(response);

        httpReplyChannel.reply(REPLY);

        Mockito.verify(response).end(REPLY);
    }


    @Test
    void replyWithAttributes() {
        final HttpServerResponse response = Mockito.mock(HttpServerResponse.class);
        final HttpReplyChannel httpReplyChannel = new HttpReplyChannel(response);

        httpReplyChannel.reply(REPLY, Map.of());

        Mockito.verify(response).end(REPLY);
    }

    @Test
    void replyError() {
        final HttpServerResponse response = Mockito.mock(HttpServerResponse.class);
        final HttpReplyChannel httpReplyChannel = new HttpReplyChannel(response);
        Mockito.when(response.setStatusCode(HttpURLConnection.HTTP_INTERNAL_ERROR)).thenReturn(response);

        httpReplyChannel.replyError(REPLY);

        Mockito.verify(response).setStatusCode(HttpURLConnection.HTTP_INTERNAL_ERROR);
        Mockito.verify(response).end(REPLY);
    }

    @Test
    void replyErrorWithAttributes() {
        final HttpServerResponse response = Mockito.mock(HttpServerResponse.class);
        final HttpReplyChannel httpReplyChannel = new HttpReplyChannel(response);
        Mockito.when(response.setStatusCode(HttpURLConnection.HTTP_INTERNAL_ERROR)).thenReturn(response);

        httpReplyChannel.replyError(REPLY, Map.of());

        Mockito.verify(response).setStatusCode(HttpURLConnection.HTTP_INTERNAL_ERROR);
        Mockito.verify(response).end(REPLY);
    }
}