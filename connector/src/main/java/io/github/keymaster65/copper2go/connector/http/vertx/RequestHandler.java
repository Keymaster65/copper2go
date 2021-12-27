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

import io.github.keymaster65.copper2go.engine.PayloadReceiver;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpServerRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestHandler implements Handler<HttpServerRequest> {

    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

    private final PayloadReceiver payloadReceiver;

    public RequestHandler(final PayloadReceiver payloadReceiver) {
        this.payloadReceiver = payloadReceiver;
    }

    @Override
    public void handle(final HttpServerRequest request) {
        log.debug("Handle request.");
        request.bodyHandler(new BodyHandler(request, payloadReceiver));
    }


}
