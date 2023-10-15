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


import net.jqwik.api.Example;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.Assumptions;
import org.crac.Context;
import org.crac.Resource;
import org.mockito.Mockito;

import java.time.Duration;
import java.util.concurrent.locks.LockSupport;

class Copper2GoGitWorkflowRepositoryTest {

    @Example
    void cracBeforeAfter() {
        @SuppressWarnings("unchecked") final Context<? extends Resource> context = Mockito.mock(Context.class);
        final Copper2GoGitWorkflowRepository copper2GoGitWorkflowRepository = new Copper2GoGitWorkflowRepository();
        Assumptions
                .assumeThat(copper2GoGitWorkflowRepository.suspended)
                .isFalse();
        Assumptions
                .assumeThat(copper2GoGitWorkflowRepository.updating)
                .isFalse();


        copper2GoGitWorkflowRepository.beforeCheckpoint(context);


        Assertions
                .assertThat(copper2GoGitWorkflowRepository.suspended)
                .isTrue();
        Assertions
                .assertThat(copper2GoGitWorkflowRepository.updating)
                .isFalse();


        copper2GoGitWorkflowRepository.afterRestore(context);


        Assertions
                .assertThat(copper2GoGitWorkflowRepository.suspended)
                .isFalse();

    }

    @Example
    void updateLocalGitRepositories() {
        final Copper2GoGitWorkflowRepository copper2GoGitWorkflowRepository = new Copper2GoGitWorkflowRepository();

        Assertions
                .assertThatNullPointerException()
                .isThrownBy(copper2GoGitWorkflowRepository::updateLocalGitRepositories);
    }

    @Example
    void waitForUdateDone() {
        @SuppressWarnings("unchecked") final Context<? extends Resource> context = Mockito.mock(Context.class);
        final Copper2GoGitWorkflowRepository copper2GoGitWorkflowRepository = new Copper2GoGitWorkflowRepository();
        copper2GoGitWorkflowRepository.updating.set(true);
        Thread.ofVirtual().start( () -> {
            LockSupport.parkNanos(Duration.ofSeconds(3).toNanos());
            copper2GoGitWorkflowRepository.updating.set(false);
        });
        Assumptions
                .assumeThat(copper2GoGitWorkflowRepository.updating)
                .isTrue();


        copper2GoGitWorkflowRepository.beforeCheckpoint(context);


        Assertions
                .assertThat(copper2GoGitWorkflowRepository.updating)
                .isFalse();
    }

    @Example
    void updateLocalGitRepositoriesWhileSuppended() {
        final Copper2GoGitWorkflowRepository copper2GoGitWorkflowRepository = new Copper2GoGitWorkflowRepository();
        copper2GoGitWorkflowRepository.suspended.set(true);


        Assertions
                .assertThatNoException()
                .isThrownBy(copper2GoGitWorkflowRepository::updateLocalGitRepositories);
    }

}