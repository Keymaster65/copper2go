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

import io.github.keymaster65.copper2go.engine.sync.engineapi.EngineException;
import io.github.keymaster65.copper2go.engine.sync.engineapi.SyncEngine;
import io.github.keymaster65.copper2go.engine.sync.workflowapi.Workflow;
import io.github.keymaster65.copper2go.engine.sync.workflowapi.WorkflowData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicReference;

public class Hello2 implements Workflow {
    private final SyncEngine engine;

    private static final Logger logger = LoggerFactory.getLogger(Hello2.class);

    private final AtomicReference<String> nameRef = new AtomicReference<>();

    public Hello2(final SyncEngine engine) {
        this.engine = engine;
    }

    @Override
    public String main(final WorkflowData workflowData) {
        try {
        logger.info("Begin workflow 2.0.");
        final long startNanos = System.nanoTime();

        logger.info("Map workflow request to workflow instance.");
        nameRef.set(Mapper.mapRequest(workflowData.getPayload()));

        logger.info("Call pricing service");
        final String priceInfo;
            priceInfo = engine.request("", nameRef.get());

        logger.info("Mapping pricing service response to workflow reply.");
            final String workflowResponse = Mapper.mapResponse(
                    nameRef.get(),
                    BusinessRules.calculatePrice(
                            startNanos,
                            System.nanoTime(),
                            Long.parseLong(priceInfo)
                    ));

        logger.info("Sending reply of workflow.");
        return workflowResponse;

        } catch (RuntimeException | EngineException e) {

            logger.info("Exceptional finish of workflow.", e);
            return e.getClass().getSimpleName() + ": " + e.getMessage();

        } finally {
            logger.info("Finish workflow.");
        }
    }
}
