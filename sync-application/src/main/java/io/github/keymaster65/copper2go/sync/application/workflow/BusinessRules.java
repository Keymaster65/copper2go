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
package io.github.keymaster65.copper2go.sync.application.workflow;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;

public class BusinessRules {

    private static final Logger logger = LoggerFactory.getLogger(BusinessRules.class);

    private BusinessRules() {
    }

    static double calculatePrice(
            final long startNanos,
            final long now,
            final long pricePerMinute
    ) {
        long durarionNanos = now - startNanos;
        logger.info("Calculate price for {} nanos.", durarionNanos);
        return Math.round(
                (double) pricePerMinute
                        / Duration.ofMinutes(1).toNanos()
                        * durarionNanos);
    }
}
