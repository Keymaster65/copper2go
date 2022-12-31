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
import io.github.keymaster65.copper2go.engine.sync.engineapi.SyncEngine;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class SyncEngineImpl implements SyncEngine {

    ConcurrentMap<String, Channel> channels = new ConcurrentHashMap<>();

    public void addRequestChannel(String channelName, HttpClient httpClient, HttpRequest.Builder builder) {
        final Channel channel = new Channel(httpClient, builder);
        channels.put(channelName, channel);
    }

    record Channel(
            HttpClient httpClient,
            HttpRequest.Builder requestBuilder
    ) {
    }

    @Override
    public String request(final String channelName, final String request) throws EngineException {
        final Channel channel = channels.get(channelName);
        HttpRequest httpRequest = channel.requestBuilder
                .POST(HttpRequest.BodyPublishers.ofString(request))
                .build();
        HttpResponse<String> response;
        try {
            response = channel.httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new EngineException("Exception while requesting.", e);
        } catch (IOException e) {
            throw new EngineException("Exception while requesting.", e);
        }

        return response.body();
    }
}
