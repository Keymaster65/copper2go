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
import io.github.keymaster65.copper2go.application.Application;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicBoolean;

public class SyncApplication implements Application {
    private static final Logger log = LoggerFactory.getLogger(SyncApplication.class);
    private final AtomicBoolean stopRequested = new AtomicBoolean(false);
    private final HttpServer httpServer;

    public SyncApplication(final HttpServer httpServer) {
        this.httpServer = httpServer;
    }
    @Override
    public synchronized void start() {
        log.info("start application");
        httpServer.start();
    }
    @Override
    public synchronized void stop() {
        log.info("stop application");
        stopRequested.set(true);
        httpServer.stop(10);
    }
    @Override
    public synchronized boolean isStopRequested() {
        return this.stopRequested.get();
    }
}