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
import net.jqwik.api.Arbitraries;
import net.jqwik.api.Arbitrary;
import net.jqwik.api.Example;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.Provide;
import org.assertj.core.api.Assertions;
import org.mockito.Mockito;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;


class ClientTest {

    @Example
    void givenLessThatMaxCall_thenParkIfToManyCallsIsFalse() {
        final boolean parkIfToManyCalls = Client.parkIfToManyCalls(
                1000,
                Duration.ofSeconds(1).toNanos(),
                999
        );

        Assertions.assertThat(parkIfToManyCalls).isFalse();
    }

    @Example
    void givenMaxCall_thenParkIfToManyCallsIsFalse() {
        final boolean parkIfToManyCalls = Client.parkIfToManyCalls(
                1000,
                Duration.ofSeconds(1).toNanos(),
                1000
        );

        Assertions.assertThat(parkIfToManyCalls).isFalse();
    }

    @Example
    void givenMoreThanMaxCall1001_thenParkIfToManyCallsIsTrue() {
        final boolean parkIfToManyCalls = Client.parkIfToManyCalls(
                1000,
                Duration.ofSeconds(1).toNanos(),
                1001
        );

        Assertions.assertThat(parkIfToManyCalls).isTrue();
    }

    @Example
    void givenMoreThanMaxCall999_thenParkIfToManyCallsIsTrue() {
        final boolean parkIfToManyCalls = Client.parkIfToManyCalls(
                999,
                Duration.ofSeconds(1).toNanos(),
                1000
        );

        Assertions.assertThat(parkIfToManyCalls).isTrue();
    }

    @Example
    void waitForRequestsToBeSent() {
        final Meter actionMeter = Mockito.mock(Meter.class);
        Mockito
                .when(actionMeter.getCount())
                .thenReturn(0L)
                .thenReturn(0L)
                .thenReturn(1L);

        final Client client = new Client(
                Mockito.mock(MetricRegistry.class),
                actionMeter,
                Mockito.mock(Meter.class),
                Mockito.mock(Meter.class),
                Mockito.mock(Timer.class),
                Mockito.mock(HttpClient.class)
        );

        final int sleepCount = client.waitForRequestsToBeSent(1);

        Assertions.assertThat(sleepCount).isEqualTo(2);
    }

    @Example
    void waitForReponsesToBeReceived() {
        final Meter okMeter = Mockito.mock(Meter.class);
        Mockito
                .when(okMeter.getCount())
                .thenReturn(0L)
                .thenReturn(0L)
                .thenReturn(1L);
        final Meter nokMeter = Mockito.mock(Meter.class);
        Mockito
                .when(nokMeter.getCount())
                .thenReturn(0L)
                .thenReturn(0L)
                .thenReturn(1L);

        final Client client = new Client(
                Mockito.mock(MetricRegistry.class),
                Mockito.mock(Meter.class),
                okMeter,
                nokMeter,
                Mockito.mock(Timer.class),
                Mockito.mock(HttpClient.class)
        );

        final int sleepCount = client.waitForReponsesToBeReceived(2);

        Assertions.assertThat(sleepCount).isEqualTo(2);

    }

    @Example
    void actionBadCode() throws IOException, InterruptedException {
        final HttpClient httpClient = Mockito.mock(HttpClient.class);
        @SuppressWarnings("unchecked") final HttpResponse<Object> httpResponse = Mockito.mock(HttpResponse.class);
        Mockito
                .when(httpResponse.statusCode())
                .thenReturn(404);
        final Meter okMeter = Mockito.mock(Meter.class);
        final Meter nokMeter = Mockito.mock(Meter.class);
        Mockito
                .when(httpClient.send(Mockito.any(HttpRequest.class), Mockito.any()))
                .thenReturn(httpResponse);
        final Client client = new Client(
                Mockito.mock(MetricRegistry.class),
                Mockito.mock(Meter.class),
                okMeter,
                nokMeter,
                Mockito.mock(Timer.class),
                httpClient
        );

        client.action("https://ignored.de");

        Mockito.verifyNoInteractions(okMeter);
        Mockito.verify(nokMeter).mark();
    }

    @SuppressWarnings("unused")
    @Provide
    Arbitrary<Exception> sendExceptions() {
        return Arbitraries.of(
                new IOException("Test IOException"),
                new InterruptedException("Test InterruptedException")
        );
    }

    @Property
    void actionSendException(@ForAll("sendExceptions") final Exception exception) throws IOException, InterruptedException {
        final HttpClient httpClient = Mockito.mock(HttpClient.class);
        final Meter okMeter = Mockito.mock(Meter.class);
        final Meter nokMeter = Mockito.mock(Meter.class);
        Mockito
                .when(httpClient.send(Mockito.any(HttpRequest.class), Mockito.any()))
                .thenThrow(exception);
        final Client client = new Client(
                Mockito.mock(MetricRegistry.class),
                Mockito.mock(Meter.class),
                okMeter,
                nokMeter,
                Mockito.mock(Timer.class),
                httpClient
        );

        client.action("https://ignored.de");

        Mockito.verifyNoInteractions(okMeter);
        Mockito.verify(nokMeter).mark();
        Mockito.verify(httpClient).send(Mockito.any(HttpRequest.class), Mockito.any());
    }

    @Example
    void actionURISyntaxException() {
        final HttpClient httpClient = Mockito.mock(HttpClient.class);
        final Meter okMeter = Mockito.mock(Meter.class);
        final Meter nokMeter = Mockito.mock(Meter.class);
        final Client client = new Client(
                Mockito.mock(MetricRegistry.class),
                Mockito.mock(Meter.class),
                okMeter,
                nokMeter,
                Mockito.mock(Timer.class),
                httpClient
        );

        final String badUri = ":///bad";
        client.action(badUri);

        Mockito.verifyNoInteractions(okMeter);
        Mockito.verify(nokMeter).mark();
    }
}