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

import com.sun.net.httpserver.HttpServer;
import net.jqwik.api.Example;
import org.assertj.core.api.Assertions;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import java.lang.management.ManagementFactory;
import java.net.InetSocketAddress;


class MainTest {
    @Example
    void givenServerStarted_whenAllServerResponses200_thenMetricsAllOk() throws Exception {
        final HttpServer localHttpServer =
                HttpServer
                        .create(new InetSocketAddress(39665), 0);
        localHttpServer.createContext(
                "/",
                exchange -> {
                    exchange.sendResponseHeaders(200, 0);
                    exchange.getResponseBody().close();
                }
        );
        localHttpServer.start();


        Main.main(new String[]{"http://localhost:59665/copper2go/3/api/twoway/2.0/Hello"});


        MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
        final ObjectName okMeter = new ObjectName("metrics:type=meters,name=ok");
        final Long okCount = (Long) mBeanServer.getAttribute(okMeter, "Count");
        Assertions.assertThat(okCount).isEqualTo(3);

        localHttpServer.stop(0);
    }

    @Example
    void constructorNoException() {
        Assertions
                .assertThatCode(Main::new)
                .doesNotThrowAnyException();
    }
}