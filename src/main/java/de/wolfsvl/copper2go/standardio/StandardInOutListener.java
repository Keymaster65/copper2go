package de.wolfsvl.copper2go.standardio;

import de.wolfsvl.copper2go.application.Application;
import de.wolfsvl.copper2go.impl.StdInOutContextImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class StandardInOutListener {

    private static final Logger log = LoggerFactory.getLogger(StandardInOutListener.class);
    public void listenLocalStream(final Application applicationapplication) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        while (1 == 1) {
            try {
                System.out.println("Enter your name: ");
                String line1 = reader.readLine();
                log.debug("line1=" + line1);
                if (line1 == null) {
                    throw new NullPointerException("Read a 'null' line. So there seems to be no stdin. Might happen when starting with gradle.");
                }
                if ("exit".equals(line1)) {
                    throw new Application.ApplicationException("Input canceled by 'exit' line.");
                }
                applicationapplication.callWorkflow(new StdInOutContextImpl(line1));
                applicationapplication.waitForIdleEngine();
            } catch (Exception e) {
                throw new Application.ApplicationException("Exception while getting input.", e);
            }
        }
    }
}
