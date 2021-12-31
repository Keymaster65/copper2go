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

import io.vertx.core.http.HttpServerResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.net.HttpURLConnection;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class LicenseHandlerTest {

    @ParameterizedTest
    @ValueSource(strings = {"/", "/.", "/notFound.html"})
    void handleLicenseNotFound(final String path) {
        HttpServerResponse response = mock(HttpServerResponse.class);
        when(response.setStatusCode(HttpURLConnection.HTTP_NOT_FOUND)).thenReturn(response);

        LicenseHandler.handleLicense(response, path);

        verify(response).setStatusCode(HttpURLConnection.HTTP_NOT_FOUND);
        verify(response).end("Exception while getting licenses from uri %s. null" .formatted(path));
    }

    @Test
    void handleLicenseOk() {
        HttpServerResponse response = mock(HttpServerResponse.class);
        when(response.setStatusCode(HttpURLConnection.HTTP_OK)).thenReturn(response);

        LicenseHandler.handleLicense(response, "/test.html");

        verify(response).setStatusCode(HttpURLConnection.HTTP_OK);
        verify(response).end(anyString());
    }

}