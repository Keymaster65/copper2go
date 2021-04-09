package de.wolfsvl.copper2go.connector.standardio;

import de.wolfsvl.copper2go.engine.Copper2GoEngine;
import de.wolfsvl.copper2go.impl.StdInOutContextImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class StandardInOutListener {

    private static final Logger log = LoggerFactory.getLogger(StandardInOutListener.class);

    public void listenLocalStream(final Copper2GoEngine applicationapplication) throws StandardInOutException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        while (true) {
            try {
                System.out.println("Enter your name: ");
                String line1 = reader.readLine();
                log.debug("line: {}", line1);
                if (line1 == null) {
                    throw new NullPointerException("Read a 'null' line. So there seems to be no stdin. Might happen when starting with gradle.");
                }
                if ("exit".equals(line1)) {
                    throw new StandardInOutException("Input canceled by 'exit' line.");
                }
                applicationapplication.callWorkflow(new StdInOutContextImpl(line1), "Hello", 1, 0);
                applicationapplication.waitForIdleEngine();
            } catch (Exception e) {
                throw new StandardInOutException("Exception while getting input.", e);
            }
        }
    }
}
