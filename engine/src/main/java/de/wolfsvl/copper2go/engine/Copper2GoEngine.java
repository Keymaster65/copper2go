package de.wolfsvl.copper2go.engine;

import org.copperengine.core.DependencyInjector;

public interface Copper2GoEngine {
    void start(DependencyInjector dependencyInjector) throws EngineException;

    void stop() throws EngineException;

    void callWorkflow(
            final String payload,
            final ReplyChannel replyChannel,
            final String workflow,
            final long major,
            final long minor
    ) throws EngineException;

    void waitForIdleEngine();

    void notify(final String correlationId, String response);

    void notifyError(final String correlationId, String response);
}
