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
package io.github.keymaster65.copper2go.connector.kafka.vertx;

import io.github.keymaster65.copper2go.engine.Copper2GoEngine;
import io.github.keymaster65.copper2go.engine.RequestChannel;

import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

public class KafkaRequestChannelImpl implements RequestChannel {

    private final Copper2GoKafkaSender copper2GoKafkaSender;
    private final Copper2GoEngine engine;

    private final AtomicLong successCount = new AtomicLong(0);
    private final AtomicLong failCount = new AtomicLong(0);

    public KafkaRequestChannelImpl(
            final Copper2GoKafkaSender copper2GoKafkaSender,
            final Copper2GoEngine engine
    ) {
        this.copper2GoKafkaSender = copper2GoKafkaSender;
        this.engine = engine;
    }

    @Override
    public void request(
            final String request,
            final Map<String, String> attributes,
            final String responseCorrelationId
    ) {
        copper2GoKafkaSender.send(request, attributes)
                .onSuccess(metadata ->
                {
                    successCount.incrementAndGet();
                    engine.notify(responseCorrelationId, "");
                })
                .onFailure(throwable ->
                        {
                            failCount.incrementAndGet();
                            engine.notifyError(responseCorrelationId, throwable.getMessage());
                        }
                );
    }

    @Override
    public void close() {
        copper2GoKafkaSender.close();
    }

    public long getSuccessCount() {
        return successCount.get();
    }

    public long getFailCount() {
        return failCount.get();
    }
}
