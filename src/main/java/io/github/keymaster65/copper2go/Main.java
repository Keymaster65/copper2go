package io.github.keymaster65.copper2go;

import io.github.keymaster65.copper2go.application.Application;
import io.github.keymaster65.copper2go.application.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.locks.LockSupport;

public class Main {
    private static final Logger log = LoggerFactory.getLogger(Main.class);
    public static final String ENV_C2G_CONFIG = "C2G_CONFIG";

    public static void main(String[] args) throws Exception {
        Application application = null;
        try {
            log.info("Begin of Main.");
            String configEnv = System.getenv(ENV_C2G_CONFIG);
            if (configEnv != null) {
                log.info("Using config defined in environment variable C2G_CONFIG.");
                application = Application.of(Config.of(configEnv));
            } else {
                log.info("Use default config.");
                application = Application.of(Config.of());
            }
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
