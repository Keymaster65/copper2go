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
package io.github.keymaster65.copper2go.engine.vanilla.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Consumer;

class EarlyResponseRunnableFactory {

    private static final Logger log = LoggerFactory.getLogger(EarlyResponseRunnableFactory.class);

    static Runnable createEarlyResponseRunnable(
            final String responseCorrelationId,
            final Consumer<String> consumer,
            final Continuation earlyResponseContinuation
    ) {
        return () -> {
            log.info("Continue early response (responseCorrelationId={}).", responseCorrelationId);
            final String response = earlyResponseContinuation.response();
            log.trace("response={}", response);
            consumer.accept(response);
        };
    }

    static Runnable createEarlyResponseRunnable(
            final String responseCorrelationId,
            final String response,
            final Continuation waitingConsumer
    ) {
        return () -> {
            log.info("Continue response (responseCorrelationId={}).", responseCorrelationId);
            log.trace("response={}", response);
            waitingConsumer.consumer().accept(response);
        };
    }

    private EarlyResponseRunnableFactory() {
    }
}