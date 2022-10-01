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
package io.github.keymaster65.copper2go.engine.sync.impl;

import io.github.keymaster65.copper2go.engine.sync.engineapi.EngineException;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import org.assertj.core.api.Assertions;
import org.mockito.Mockito;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

class SyncEngineImplTest {

    @Property
    void request(@ForAll String requestPayload) throws IOException, InterruptedException, EngineException {
        final SyncEngineImpl syncEngineImpl = new SyncEngineImpl();
        final HttpRequest.Builder builder = Mockito.mock(HttpRequest.Builder.class);
        final HttpClient httpClient = Mockito.mock(HttpClient.class);
        syncEngineImpl.addRequestChannel(
                "",
                httpClient,
                builder
        );
        @SuppressWarnings("unchecked") HttpResponse<String> response = Mockito.mock(HttpResponse.class);
        final String responseBody = "responseBody";
        Mockito.when(response.body()).thenReturn(responseBody);
        Mockito.when(builder.POST(Mockito.any())).thenReturn(builder);
        //noinspection unchecked
        Mockito
                .when(httpClient.send(Mockito.any(), (HttpResponse.BodyHandler<String>) Mockito.any()))
                .thenReturn(response);


        final String responseString = syncEngineImpl.request("", requestPayload);


        Assertions.assertThat(responseString).isEqualTo(responseBody);
    }

}