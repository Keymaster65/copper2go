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

import com.google.common.io.CharStreams;
import io.vertx.core.http.HttpServerResponse;

import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class LicenseHandler {

    public static final String LICENSE_INDEX_HTML = "license/index.html";
    public static final String LICENSE_PATH = "license";

    private LicenseHandler() {}

    public static void handleLicense(final HttpServerResponse response, final String uri) {
        try {
            String path;
            if ("/".equals(uri) || "/.".equals(uri)) {
                path = LICENSE_INDEX_HTML;
            } else {
                path = LICENSE_PATH + uri;
            }

            try (Reader reader = new InputStreamReader(Objects.requireNonNull(ClassLoader.getSystemClassLoader().getResourceAsStream(path)), StandardCharsets.UTF_8)) {
                response
                        .setStatusCode(HttpURLConnection.HTTP_OK)
                        .end(CharStreams.toString(reader));
            }
        } catch (Exception e) {
            response
                    .setStatusCode(HttpURLConnection.HTTP_NOT_FOUND)
                    .end(String.format("Exception while getting licenses from uri %s. %s", uri, e.getMessage()));
        }
    }
}
