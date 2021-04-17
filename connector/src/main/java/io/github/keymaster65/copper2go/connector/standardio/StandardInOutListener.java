package io.github.keymaster65.copper2go.connector.standardio;

import io.github.keymaster65.copper2go.engine.Copper2GoEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class StandardInOutListener {

    private static final Logger log = LoggerFactory.getLogger(StandardInOutListener.class);

    public void listenLocalStream(final Copper2GoEngine copper2GoEngine) throws StandardInOutException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        while (true) { // NOSONAR
            try {
                System.out.println("Enter your name: "); // NOSONAR
                String line1 = reader.readLine();
                log.debug("line: {}", line1);
                if (line1 == null) {
                    throw new NullPointerException("Read a 'null' line. So there seems to be no stdin. Might happen when starting with gradle.");
                }
                if ("exit".equals(line1)) {
                    throw new StandardInOutException("Input canceled by 'exit' line.");
                }
                copper2GoEngine.callWorkflow(line1, new StandardInOutReplyChannelImpl(), "Hello", 1, 0);
                copper2GoEngine.waitForIdleEngine();
            } catch (Exception e) {
                throw new StandardInOutException("Exception while getting input.", e);
            }
        }
    }
}
