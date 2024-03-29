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
package io.github.keymaster65.copper2go.connector.http.vertx.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.keymaster65.copper2go.connector.http.HttpMethod;

public class HttpRequestChannelConfig {

    public final String host;
    public final int port;
    public final String path;
    public final HttpMethod method;

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public HttpRequestChannelConfig(
            @JsonProperty(required = true, value = "host") final String host,
            @JsonProperty(required = true, value = "port") final int port,
            @JsonProperty(required = true, value = "path") final String path,
            @JsonProperty(required = true, value = "method") final String method
    ) {
        this.host = host;
        this.port = port;
        this.path = path;
        this.method = HttpMethod.valueOf(method);
    }
}
