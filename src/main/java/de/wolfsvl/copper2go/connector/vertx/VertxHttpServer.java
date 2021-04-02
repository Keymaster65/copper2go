package de.wolfsvl.copper2go.connector.vertx;

import de.wolfsvl.copper2go.engine.Copper2GoEngine;
import de.wolfsvl.copper2go.connector.Copper2GoHttpServer;
import de.wolfsvl.copper2go.impl.HttpContextImpl;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import org.copperengine.core.CopperException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;

public class VertxHttpServer implements Copper2GoHttpServer {

    private static final Logger log = LoggerFactory.getLogger(VertxHttpServer.class);

    private final HttpServer httpServer;
    private final Vertx vertx;
    private final int port;


    public VertxHttpServer(final int port, final Copper2GoEngine copper2GoEngine) {
        this(port, copper2GoEngine, Vertx.vertx());
    }

    VertxHttpServer(final int port, final Copper2GoEngine copper2GoEngine, final Vertx vertx) {
        this(port, copper2GoEngine, vertx, vertx.createHttpServer());
    }

    VertxHttpServer(final int port, final Copper2GoEngine copper2GoEngine, final Vertx vertx, final HttpServer httpServer) {
        this.port = port;
        this.vertx = vertx;
        this.httpServer = httpServer;
        httpServer.requestHandler(
                request -> request.handler(buffer -> {
                    final String requestBody;
                    requestBody = new String(buffer.getBytes(), StandardCharsets.UTF_8);
                    try {
                        copper2GoEngine.callWorkflow(new HttpContextImpl(requestBody, request.response()));
                    } catch (CopperException e) {
                        request.response().end(String.format("Exception: %s", e.getMessage()));
                        log.warn("Exception while calling workflow.", e);
                    }
                }));
    }


    public void start() {
        httpServer.listen(port);
    }

    public void stop() {
        httpServer.close(e -> log.info("Server stopped. e={}", e.succeeded()));
        vertx.close();
    }

}
