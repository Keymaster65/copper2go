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
package io.github.keymaster65.copper2go.pricingsimulator;

import com.sun.net.httpserver.HttpServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.LockSupport;


public class PricingServer implements AutoCloseable {

    public static final int DEFAULT_HTTP_PORT = 39665;
    public static final Duration DEFAULT_DELAY = Duration.ofSeconds(10);
    public static final String HELP_OPTION = "-h";
    private static final Logger log = LoggerFactory.getLogger(PricingServer.class);
    public static final Duration DEFAULT_TIME_TO_LIVE = Duration.ofMillis(0);
    private ExecutorService executorService;
    private HttpServer httpServer;
    private DelayingHandler delayingHandler;


    public synchronized PricingServer start(final String[] args) throws IOException {
        if (args.length > 0 && args[0].equals(HELP_OPTION)) {
            throw new HelpException(
                    "Usage: Main -h|[SERVICE_DAYS [PARK|SLEEP|WAIT [DELAY_MILLIS [HTTP_PORT]]]]"
            );
        }
        final PricingServer pricingServer = start(
                getDelay(args),
                getDelayMode(args),
                getPort(args)
        );
        final Duration timeToLive = getTimeToLive(args);
        log.info("Serving for {}.", timeToLive);
        LockSupport.parkNanos(timeToLive.toNanos());
        return pricingServer;
    }

    public synchronized PricingServer start(
            final Duration delay,
            final DelayerFactory.Mode delayMode,
            final int port
    ) throws IOException {
        executorService = Executors.newVirtualThreadPerTaskExecutor();
        delayingHandler = new DelayingHandler(delayMode, delay);
        httpServer = createHttpServer(
                port,
                executorService,
                delayingHandler
        );
        Thread.ofVirtual().start(httpServer::start);
        delayingHandler.start();
        return this;
    }

    private static HttpServer createHttpServer(
            final int port,
            final ExecutorService executorService,
            final DelayingHandler delayingHandler
    ) throws IOException {
        final HttpServer localHttpServer = HttpServer.create(new InetSocketAddress(port), 0);
        localHttpServer.setExecutor(executorService);
        localHttpServer.createContext("/", delayingHandler);
        return localHttpServer;
    }

    @Override
    public synchronized void close() {
        executorService.close();
        delayingHandler.stop();
        httpServer.stop(0);
    }
    Duration getTimeToLive(final String[] args) {
        if (args.length > 0) {
            return Duration.ofDays(Long.parseLong(args[0]));
        }
        return DEFAULT_TIME_TO_LIVE;
    }

    DelayerFactory.Mode getDelayMode(final String[] args) {
        if (args.length > 1) {
            return DelayerFactory.Mode.valueOf(args[1]);
        }
        return DelayerFactory.Mode.PARK;
    }

    Duration getDelay(final String[] args) {
        if (args.length > 2) {
            return Duration.ofMillis(Long.parseLong(args[2]));
        }
        return DEFAULT_DELAY;
    }

    int getPort(final String[] args) {
        if (args.length > 3) {
            return Integer.parseInt(args[3]);
        }
        return DEFAULT_HTTP_PORT;
    }
}
