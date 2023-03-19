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

import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import com.codahale.metrics.jmx.JmxReporter;
import com.sun.net.httpserver.HttpServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.LockSupport;

public class PricingSimulator {

    private static final MetricRegistry metricRegistry = new MetricRegistry();
    private static final Timer timer = metricRegistry.timer("responses");

    // -Dcom.sun.management.jmxremote.port=10080 -Dcom.sun.management.jmxremote.authenticate=false -Dcom.sun.management.jmxremote.ssl=false -Dcom.sun.management.jmxremote.local.port=10081 -Dcom.sun.management.jmxremote=true -Djdk.virtualThreadScheduler.maxPoolSize=2100 -Djdk.virtualThreadScheduler.parallelism=2000
    private static final Logger log = LoggerFactory.getLogger(PricingSimulator.class);

    public static void main(String[] args) throws Exception {
        log.info("Starting HTTP Server");

//        System.setProperty("com.sun.management.jmxremote.port", "10080");
//        System.setProperty("com.sun.management.jmxremote.authenticate", "false");
//        System.setProperty("com.sun.management.jmxremote.ssl", "false");
//        System.setProperty("com.sun.management.jmxremote", "true");
//        System.setProperty("jdk.virtualThreadScheduler.maxPoolSize", "2100");
//        System.setProperty("jdk.virtualThreadScheduler.parallelism", "2000");

        JmxReporter reporter = JmxReporter.forRegistry(metricRegistry).build();
        reporter.start();

        try (final ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor()) {
            final HttpServer httpServer = HttpServer.create(new InetSocketAddress(39665), 0);
            httpServer.setExecutor(executorService);
            httpServer.createContext("/",
                    exchange -> {
                        try (final Timer.Context ignored = timer.time()) {
                            log.info("Received request.");
                            final Duration delay = Duration.ofMillis(10000);
                            LockSupport.parkNanos(delay.toNanos());
//                        try {
//                            final Object lock = new Object();
//                            synchronized (lock) {
//                                lock.wait(10000);
//                            }
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
                            try (OutputStream responseBody = exchange.getResponseBody()) {
                                final byte[] reponseBytes = "42".getBytes(StandardCharsets.UTF_8);
                                exchange.sendResponseHeaders(200, reponseBytes.length);
                                responseBody.write(reponseBytes);
                            }
                            log.debug("Sent response.");
//                        exchange.close(); // TODO: Is this needed?
                        }
                    });
            httpServer.start();
            Thread.sleep(12000000);
        }
    }

    private PricingSimulator() {
    }
}
