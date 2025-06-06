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
package io.github.keymaster65.copper2go.clientsimulator;

import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import com.codahale.metrics.jmx.JmxReporter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.LockSupport;

public class Client {
    public static final int EXPECTED_HTTP_CODE = 200;
    private static final String DEFAULT_URL = "http://localhost:39665/copper2go/3/api/twoway/1.0/Pricing"; // direct PricingSimulator
    private static final String PAYLOAD = "wolf";
    private static final Logger log = LoggerFactory.getLogger(Client.class);
    private final MetricRegistry metricRegistry;
    private final Meter actionMeter;
    private final Meter okMeter;
    private final Meter nokMeter;
    private final Timer timer;
    private final HttpClient httpClient;

    public Client() {
        this(
                new MetricRegistry()
        );
    }

    Client(
            final MetricRegistry metricRegistry
    ) {
        this(
                metricRegistry,
                metricRegistry.meter("action"),
                metricRegistry.meter("ok"),
                metricRegistry.meter("nok"),
                metricRegistry.timer("responses"),
                HttpClient.newBuilder()
                        .version(HttpClient.Version.HTTP_2)
                        .followRedirects(HttpClient.Redirect.NORMAL)
                        .connectTimeout(Duration.ofSeconds(10))
                        .build()
        );
    }

    Client(
            final MetricRegistry metricRegistry,
            final Meter actionMeter,
            final Meter okMeter,
            final Meter nokMeter,
            final Timer timer,
            final HttpClient httpClient
    ) {
        this.metricRegistry = metricRegistry;
        this.actionMeter = actionMeter;
        this.okMeter = okMeter;
        this.nokMeter = nokMeter;
        this.timer = timer;
        this.httpClient = httpClient;
    }

    public void start() {
        @SuppressWarnings("resource")
        JmxReporter reporter = JmxReporter.forRegistry(metricRegistry).build();
        reporter.start();
        start(
                3,
                1,
                DEFAULT_URL
        );
    }

    public void start(
            final long testDurationSecond,
            final long callsPerSecond,
            final String serverUrl
    ) {
        log.info("Start with callsPerSecond={}", callsPerSecond);
        try (final ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor()) {

            final long startNanos = System.nanoTime();
            final long calls = testDurationSecond * callsPerSecond;

            for (long callsCount = 0; callsCount < calls; callsCount++) {
                parkIfToManyCalls(
                        callsPerSecond,
                        System.nanoTime() - startNanos,
                        callsCount
                );
                executorService.submit(() -> this.action(serverUrl));
            }

            waitForRequestsToBeSent(calls);
            waitForReponsesToBeReceived(calls);
        }
    }

    void action(final String serverUrl) {
        actionMeter.mark();
        try (final var _ = timer.time()) {
            try {
                final HttpResponse<String> httpResponse = callService(serverUrl);
                final int statusCode = httpResponse.statusCode();
                if (statusCode == EXPECTED_HTTP_CODE) {
                    okMeter.mark();
                } else {
                    log.warn("Received status code {}.", statusCode);
                    nokMeter.mark();
                }
            } catch (URISyntaxException | IOException e) {
                nokMeter.mark();
                log.warn("Exception in action.", e);
            } catch (InterruptedException e) {
                nokMeter.mark();
                log.warn("InterruptedException in action.", e);
                Thread.currentThread().interrupt();
            }
        }
    }

    private HttpResponse<String> callService(final String serverUrl) throws URISyntaxException, IOException, InterruptedException {
        final HttpRequest httpRequest =
                HttpRequest.newBuilder()
                        .timeout(Duration.ofSeconds(50))
                        .uri(new URI(serverUrl))
                        .POST(HttpRequest.BodyPublishers.ofString(PAYLOAD))
                        .build();
        return httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
    }

    static boolean parkIfToManyCalls(
            final long callsPerSecond,
            final long runNanos,
            final long callsCount
    ) {
        if (
                Duration.ofSeconds(1).toNanos() * (callsCount) > runNanos * callsPerSecond
        ) {
            log.info(
                    "Load is {}. Parking, because {} calls in {}.",
                    (double) callsCount * Duration.ofSeconds(1).toNanos() / runNanos,
                    callsCount,
                    Duration.ofNanos(runNanos)
            );
            LockSupport.parkNanos(Duration.ofMillis(1000).toNanos());
            return true;
        }
        return false;
    }

    int waitForRequestsToBeSent(final long calls) {
        int sleepCount = 0;
        while (actionMeter.getCount() < calls) {
            sleepCount++;
            LockSupport.parkNanos(Duration.ofSeconds(1).toNanos());
            log.info("Wait for requests to be sent.");
        }
        return sleepCount;
    }

    int waitForReponsesToBeReceived(final long calls) {
        int sleepCount = 0;
        while (okMeter.getCount() + nokMeter.getCount() < calls) {
            sleepCount++;
            LockSupport.parkNanos(Duration.ofSeconds(1).toNanos());
            log.info("Wait for responses to be received.");
        }
        return sleepCount;
    }
}
