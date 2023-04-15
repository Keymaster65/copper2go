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

import com.codahale.metrics.Gauge;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import com.codahale.metrics.jmx.JmxReporter;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;

public class DelayingHandler implements HttpHandler {

    private final AtomicInteger activeRequestCount = new AtomicInteger(0);
    private final MetricRegistry metricRegistry = new MetricRegistry();
    private final Timer timer = metricRegistry.timer("responses");
    private static final Logger log = LoggerFactory.getLogger(DelayingHandler.class);
    private final Duration delay;
    private final JmxReporter reporter;
    private final Delayer delayer;

    DelayingHandler(final DelayerFactory.Mode delayMode, final Duration delay) {
        this.delay = delay;
        metricRegistry.register(
                MetricRegistry.name(DelayingHandler.class, "activeRequestCount"),
                (Gauge<Integer>) activeRequestCount::get
        );
        reporter = JmxReporter.forRegistry(metricRegistry).build();
        delayer = DelayerFactory.create(delayMode);

    }

    @Override
    public void handle(final HttpExchange exchange) throws IOException {
        try (final Timer.Context ignored = timer.time()) {
            log.info("Received request.");
            activeRequestCount.incrementAndGet();

            try (OutputStream responseBody = exchange.getResponseBody()) {
                delayer.delay(delay);
                final byte[] reponseBytes = "42".getBytes(StandardCharsets.UTF_8);
                exchange.sendResponseHeaders(200, reponseBytes.length);
                responseBody.write(reponseBytes);
                log.info("Sent response.");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new IOException("I/O interrupted.", e);
            } finally {
                activeRequestCount.decrementAndGet();
            }
        }

    }

    public synchronized void start() {
        reporter.start();
    }

    public synchronized void stop() {
        reporter.stop();
    }
}
