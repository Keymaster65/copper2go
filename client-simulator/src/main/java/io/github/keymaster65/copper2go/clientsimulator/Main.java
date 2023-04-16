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

public class Main {

    private final MetricRegistry metricRegistry = new MetricRegistry();
    private final Meter actionMeter = metricRegistry.meter("action");
    private final Meter okMeter = metricRegistry.meter("ok");
    private final Meter nokMeter = metricRegistry.meter("nok");

    private final Timer timer = metricRegistry.timer("responses");

    private static final String DEFAULT_URL = "http://localhost:39665/copper2go/3/api/twoway/1.0/Pricing"; // direct PricingSimulator
    private static final String PAYLOAD = "wolf";

    private static final Logger log = LoggerFactory.getLogger(Main.class);
    private final HttpClient client = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_2)
            .followRedirects(HttpClient.Redirect.NORMAL)
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    public static void main(String[] args) throws Exception {

        try (final ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor()) {
            final Main clientSimulator = new Main();
            @SuppressWarnings("resource") // need it for test
            JmxReporter reporter = JmxReporter.forRegistry(clientSimulator.metricRegistry).build();
            reporter.start();

            final long testDurationSecond = 3;
            final long callsPerSecond = 1;
            final long intervalMillis = 1000 / callsPerSecond;
            log.info("callsPerSecond={}", callsPerSecond);

            final long start = System.nanoTime();
            final long calls = testDurationSecond * callsPerSecond;
            for (long i = 0; i < calls; i++) {
                if (
                        1000_000_000D * (i - callsPerSecond) > (System.nanoTime() - start) * callsPerSecond
                ) {
                    log.trace("mean={} > callsPerSecond={}.", clientSimulator.actionMeter.getMeanRate(), callsPerSecond);
                    log.trace("Wait {} for interval {} millis at {}.", intervalMillis, i, System.currentTimeMillis());
                    LockSupport.parkNanos(Duration.ofMillis(intervalMillis).toNanos());
                    log.trace("Unparked at {}.", System.currentTimeMillis());
                }
                log.trace("[{} vs. {}] per {};  mean={} > callsPerSecond={}.", i, clientSimulator.actionMeter.getCount(), (System.nanoTime() - start), clientSimulator.actionMeter.getMeanRate(), callsPerSecond);
                log.trace("mean={}; count={}", clientSimulator.actionMeter.getMeanRate(), clientSimulator.actionMeter.getCount());
                executorService.submit(clientSimulator::action);
            }

            while (clientSimulator.actionMeter.getCount() < calls) {
                Thread.sleep(Duration.ofSeconds(1));
                log.info("Wait for requests to be sent.");
            }

            while (clientSimulator.okMeter.getCount() + clientSimulator.nokMeter.getCount() < calls) {
                Thread.sleep(Duration.ofSeconds(1));
                log.info("Wait for responses to be received.");
            }
        }
    }

    private void action() {
        actionMeter.mark();
        try (final Timer.Context ignored = timer.time()) {
            try {
                final HttpResponse<String> httpResponse = callService();
                final int statusCode = httpResponse.statusCode();
                if (statusCode == 200) {
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

    private HttpResponse<String> callService() throws URISyntaxException, IOException, InterruptedException {
        final HttpRequest httpRequest =
                HttpRequest.newBuilder()
                        .timeout(Duration.ofSeconds(50))
                        .uri(new URI(DEFAULT_URL))
                        .POST(HttpRequest.BodyPublishers.ofString(PAYLOAD))
                        .build();
        return client.send(httpRequest, HttpResponse.BodyHandlers.ofString());
    }

    private Main() {
    }
}
