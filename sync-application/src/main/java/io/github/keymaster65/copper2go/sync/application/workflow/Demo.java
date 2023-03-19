package io.github.keymaster65.copper2go.sync.application.workflow;

import java.time.Duration;

public class Demo {
    private final Thread thread;

    Demo() {
        thread = Thread.currentThread();
    }

    boolean sleeping = false;

    void demo() throws IllegalAccessException, InterruptedException {
        //log.debug("thread={} currentThread={} all={}", thread, Thread.currentThread(),  Thread.getAllStackTraces());
        if (Thread.currentThread() != thread) {
            //throw new IllegalAccessException("Access with one thread only");
        }

        sleeping = true;
        while (sleeping){
            //log.info("Waiting for thread{} to awake.", thread);
        }
        Thread.sleep(Duration.ofSeconds(3));
        sleeping = false;
    }

}
