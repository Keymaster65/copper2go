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
package io.github.keymaster65.copper2go.engine.scotty;

import org.copperengine.ext.wfrepo.git.GitWorkflowRepository;
import org.crac.Context;
import org.crac.Core;
import org.crac.Resource;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.LockSupport;

public class Copper2GoGitWorkflowRepository extends GitWorkflowRepository implements Resource {

    private final AtomicBoolean suspended = new AtomicBoolean(false);
    private final AtomicBoolean updating = new AtomicBoolean(false);

    private static final Logger log = LoggerFactory.getLogger(Copper2GoGitWorkflowRepository.class);

    Copper2GoGitWorkflowRepository() {
        Core.getGlobalContext().register(this);
    }

    @Override
    protected synchronized void updateLocalGitRepositories() throws GitAPIException, IOException {
        if (!suspended.get()) {
            updating.set(true);
            try {
                log.debug("Call updateLocalGitRepositories.");
                super.updateLocalGitRepositories();
            } finally {
                updating.set(false);
            }
        } else {
            log.warn("Suspended call of updateLocalGitRepositories.");
        }
    }

    @Override
    public void beforeCheckpoint(final Context<? extends Resource> context) {
        log.info("beforeCheckpoint is called.");
        suspended.set(true);
        while (updating.get()) {
            log.info("Waiting for update is done.");
            LockSupport.parkNanos(Duration.ofMillis(10).toNanos());
        }
        log.info("Waiting to close connections.");
        LockSupport.parkNanos(Duration.ofSeconds(3).toNanos());
    }

    @Override
    public void afterRestore(final Context<? extends Resource> context) {
        log.info("afterRestore is called.");
        suspended.set(false);
    }
}
