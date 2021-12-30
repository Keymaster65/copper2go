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
package io.github.keymaster65.copper2go.connector.http.vertx;

import io.github.keymaster65.copper2go.api.connector.ReplyChannel;
import io.vertx.core.http.HttpServerResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.HttpURLConnection;
import java.util.Map;

public class HttpReplyChannelImpl implements ReplyChannel {
    private final HttpServerResponse response;
    private static final Logger log = LoggerFactory.getLogger(HttpReplyChannelImpl.class);
    public HttpReplyChannelImpl(final HttpServerResponse response) {
        this.response = response;
    }

    @Override
    public void reply(String message, final Map<String, String> attributes) {
        if (attributes != null) {
            log.warn("Ignore attributes {}", attributes);
        }
        response.end(message);
    }

    @Override
    public void replyError(String message, final Map<String, String> attributes) {
        if (attributes != null) {
            log.warn("Ignore attributes {}", attributes);
        }
        response
                .setStatusCode(HttpURLConnection.HTTP_INTERNAL_ERROR)
                .end(message);
    }
}
