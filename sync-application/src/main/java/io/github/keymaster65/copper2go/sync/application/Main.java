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
package io.github.keymaster65.copper2go.sync.application;

import com.sun.net.httpserver.HttpServer; // NOSONAR
import io.github.keymaster65.copper2go.application.ApplicationLauncher;
import io.github.keymaster65.copper2go.engine.sync.impl.SyncEngineImpl;

import java.net.InetSocketAddress;
import java.net.URI;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {

    // tested in system
    public static void main(String[] args) throws Exception {
        try (final ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor()) {
            new ApplicationLauncher(
                    new SyncApplicationFactory(
                            HttpServer.create(new InetSocketAddress(59665), 0),
                            new SyncEngineImpl(),
                            executorService,
                            new URI("http://localhost:59665/Pricing") // NOSONAR
                    ).create()).start();
        }
    }

    private Main() {
    }
}
