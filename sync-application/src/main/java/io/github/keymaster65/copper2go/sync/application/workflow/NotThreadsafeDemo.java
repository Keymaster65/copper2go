package io.github.keymaster65.copper2go.sync.application.workflow;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.HashSet;
import java.util.Set;

public class NotThreadsafeDemo {

    private static final Logger log = LoggerFactory.getLogger(NotThreadsafeDemo.class);

    public static void main(String[] args) throws InterruptedException, IllegalAccessException {
        final int numberOfThread = 10000;
        log.info("Start {} with {} threads.", NotThreadsafeDemo.class.getSimpleName(), numberOfThread);
        final Set<Thread> threads = new HashSet<>();
        for (int i = 0; i < numberOfThread; i++) {
            final Demo demo = new Demo();

            final Thread demoThread =
                    Thread
                            .ofVirtual()
//                            .ofPlatform()
                            .name("Demo")
                            .start(() -> NotThreadsafeDemo.demoSequence(demo));
            while (!demo.sleeping){
                //log.info("Waiting for thread{} to sleep.", demoThread);
            }
            demo.sleeping = false;
            log.info("Add thread {}", demoThread);
            threads.add(demoThread);
        }
        threads.forEach(thread -> {
            try {
                log.info("Join thread {}.", thread);
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

    }

    public static void demoSequence(final Demo demo) {
        try {
            demo.demo();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
