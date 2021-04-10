/*
 * Copyright 2019 Wolf Sluyterman van Langeweyde
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
package de.wolfsvl.copper2go.impl;

import de.wolfsvl.copper2go.workflowapi.ReplyChannel;
import io.vertx.core.http.HttpServerResponse;

public class HttpReplyChannelImpl implements ReplyChannel {
    private final HttpServerResponse response;

    public HttpReplyChannelImpl(final HttpServerResponse response) {
        this.response = response;
    }

    public void reply(String message) {
        response.end(message);
    }
}