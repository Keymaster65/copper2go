package io.github.keymaster65.copper2go.engine.vanilla;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

public class ExecutorServices {

    private static final Logger log = LoggerFactory.getLogger(ExecutorServices.class);

    public static ExecutorService start() {
        return Executors.newSingleThreadScheduledExecutor();
    }

    public static boolean stop(final ExecutorService executorService) {
        log.debug("Start parking.");
        LockSupport.parkNanos(Duration.ofSeconds(10).toNanos());
        log.debug("End parking.");
        executorService.shutdown();
        try {
            return executorService.awaitTermination(30, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            log.warn("Ignore InterruptedException.", e);
            final boolean ignored = Thread.currentThread().isInterrupted();
        }
        return false;
    }
}
