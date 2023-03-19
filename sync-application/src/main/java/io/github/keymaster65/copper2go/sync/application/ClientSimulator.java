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

import com.codahale.metrics.ConsoleReporter;
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
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

public class ClientSimulator {

    final private MetricRegistry metricRegistry = new MetricRegistry();
    final private Meter actionMeter = metricRegistry.meter("action");
    final private Meter okMeter = metricRegistry.meter("ok");
    final private Meter nokMeter = metricRegistry.meter("nok");

    private final Timer timer = metricRegistry.timer("responses");

        final String uri = "http://localhost:59665/copper2go/3/api/twoway/2.0/Hello";
//    final String uri = "http://localhost:39665/copper2go/3/api/twoway/1.0/Pricing"; // direct PricingSimulator
    final String payload = "wolf";

    private static final Logger log = LoggerFactory.getLogger(ClientSimulator.class);
    private HttpClient client = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_2)
            .followRedirects(HttpClient.Redirect.NORMAL)
            .connectTimeout(Duration.ofSeconds(10))
            .build();

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
            } catch (URISyntaxException | IOException | InterruptedException e) {
                nokMeter.mark();
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }
    }

    private HttpResponse<String> callService() throws URISyntaxException, IOException, InterruptedException {
        HttpRequest httpRequest =
                HttpRequest.newBuilder()
                        .timeout(Duration.ofSeconds(50))
                        .uri(new URI(uri))
                        .POST(HttpRequest.BodyPublishers.ofString(payload))
                        .build();
        return client.send(httpRequest, HttpResponse.BodyHandlers.ofString());
    }

    public static void main(String[] args) throws Exception {

        final ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor();


        final ClientSimulator clientSimulator = new ClientSimulator();
        //warmup(clientSimulator);
        JmxReporter reporter = JmxReporter.forRegistry(clientSimulator.metricRegistry).build();
        reporter.start();
//        ConsoleReporter reporter = ConsoleReporter.forRegistry(clientSimulator.metricRegistry).build();
//        reporter.start(3, TimeUnit.SECONDS);
//        reporter.report();

        final long testDurationSecond = 600;
        final long callsPerSecond = 100;
        final long intervalMillis = 1000 / callsPerSecond;
        log.info("callsPerSecond={}", callsPerSecond);

        final long start = System.nanoTime();
        final long calls = testDurationSecond * callsPerSecond;
        for (long i = 0; i < calls; i++) {
            //final long wait = start + (i * intervalMillis) - System.currentTimeMillis();
            final long wait = intervalMillis;
            if (
                    1000_000_000D * (i -callsPerSecond) > (System.nanoTime() - start)  * callsPerSecond
            ) {
                log.trace("mean={} > callsPerSecond={}.", clientSimulator.actionMeter.getMeanRate(), callsPerSecond);
                log.trace("Wait {} for interval {} millis at {}.", intervalMillis, i, System.currentTimeMillis());
                LockSupport.parkNanos(Duration.ofMillis(wait).toNanos());
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
        reporter.stop();
        executorService.shutdown();
        executorService.awaitTermination(120, TimeUnit.SECONDS);
        if (executorService.isTerminated()) {
            log.info("ExecutorService is terminated.");
        } else {
            log.warn("ExecutorService is NOT terminated.");
        }
    }

    private static void warmup(final ClientSimulator clientSimulator) throws InterruptedException {
        log.info("Start warmup.");
        final ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor();

        for (int i = 0; i < 10; i++) {
            executorService.submit(clientSimulator::callService);
        }
        executorService.shutdown();
        log.info("Wait for executorService to terminate.");
        executorService.awaitTermination(120, TimeUnit.SECONDS);
        if (executorService.isTerminated()) {
            log.info("ExecutorService is terminated.");
        } else {
            log.warn("ExecutorService is NOT terminated.");
        }
        log.info("Warmup finished.");
    }

    private ClientSimulator() {
    }
}
