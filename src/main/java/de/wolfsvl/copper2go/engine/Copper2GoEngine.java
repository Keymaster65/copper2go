package de.wolfsvl.copper2go.engine;

import de.wolfsvl.copper2go.workflowapi.Context;
import org.copperengine.core.CopperException;

import javax.management.InstanceNotFoundException;
import javax.management.MBeanRegistrationException;

public interface Copper2GoEngine {
    void start() throws EngineException;
    void stop() throws EngineException;
    void callWorkflow(final Context context) throws CopperException;
    void waitForIdleEngine();
}
