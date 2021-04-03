package de.wolfsvl.copper2go.engine;

import de.wolfsvl.copper2go.workflowapi.Context;
import org.copperengine.core.CopperException;

public interface Copper2GoEngine {
    void start() throws EngineException;
    void stop() throws EngineException;
    void callWorkflow(final Context context) throws EngineException;
    void waitForIdleEngine();
}
