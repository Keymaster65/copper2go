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
package io.github.keymaster65.copper2go;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.locks.LockSupport;

class MainTest {

    private static final Logger log = LoggerFactory.getLogger(MainTest.class);

    @Test
    void mainStartStop() throws Exception {
        final Main main = new Main();
        new Thread(() -> MainTest.stopDelayed(main)).start();
        main.start();
    }

    private static void stopDelayed(final Main main) {
        try {
            while (!main.stop()) {
                LockSupport.parkNanos( 500 * 1000 * 1000);
            }
        } catch (Exception e) {
            log.error("Ignore exception.", e);
        }
    }
}