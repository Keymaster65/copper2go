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