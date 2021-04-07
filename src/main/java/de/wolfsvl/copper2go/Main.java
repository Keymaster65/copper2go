package de.wolfsvl.copper2go;

import de.wolfsvl.copper2go.application.Application;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.locks.LockSupport;

public class Main {
    private static final Logger log = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws Exception {
        Application application = null;
        try {
            log.info("Begin of Main.");
            application = new Application(args);
            application.start();
        } catch (Exception e) {
            log.error("Exception in application main. Try to stop application.", e);
            if (application != null) {
                application.stop();
            }
        } finally {
            if (application != null) {
                while (!application.isStopRequested()) {
                    LockSupport.parkNanos(100L * 1000L * 1000L * 1000L);
                }
            }
            log.info("End of Main.");
        }
    }
}
