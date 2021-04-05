package de.wolfsvl.copper2go.engine;

import de.wolfsvl.copper2go.workflowapi.Context;
import org.copperengine.core.Acknowledge;
import org.copperengine.core.CopperException;
import org.copperengine.core.DependencyInjector;
import org.copperengine.core.Response;

public interface Copper2GoEngine {
    void start(DependencyInjector dependencyInjector) throws EngineException;
    void stop() throws EngineException;
    void callWorkflow(final Context context) throws EngineException;
    void waitForIdleEngine();
    void notify(final String correlationId, String response);
    void notifyError(final String correlationId, String response);
}
