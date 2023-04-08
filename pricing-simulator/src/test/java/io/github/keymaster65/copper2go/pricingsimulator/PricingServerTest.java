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

import net.jqwik.api.Example;
import org.assertj.core.api.Assertions;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import java.lang.management.ManagementFactory;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class PricingServerTest {
    private final HttpClient client = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_2)
            .followRedirects(HttpClient.Redirect.NORMAL)
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    @Example
    void givenStartedService_whenServiceCalled_thenResponseOk_andMBeanCounts() throws
            Exception {
        try (final PricingServer ignored =
                     new PricingServer()
                             .start(new String[]{"0", "0", String.valueOf(PricingServer.DEFAULT_HTTP_PORT)})) {
            MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
            final ObjectName objectName = new ObjectName("metrics:type=timers,name=responses");
            final Long countBefore = (Long) mBeanServer.getAttribute(objectName, "Count");


            Assertions.assertThat(countBefore).isZero();

            final HttpRequest httpRequest =
                    HttpRequest.newBuilder()
                            .timeout(Duration.ofSeconds(50))
                            .version(HttpClient.Version.HTTP_2)
                            .uri(new URI("http://localhost:" + PricingServer.DEFAULT_HTTP_PORT + "/"))
                            .POST(HttpRequest.BodyPublishers.ofString("Test"))
                            .build();
            final HttpResponse<String> httpResponse = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());


            final Long count = (Long) mBeanServer.getAttribute(objectName, "Count");
            Assertions.assertThat(count).isOne();
            Assertions.assertThat(httpResponse.statusCode()).isEqualTo(200);
        }
    }
}
