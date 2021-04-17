package de.wolfsvl.copper2go.connector.http.vertx;

import de.wolfsvl.copper2go.connector.http.Copper2GoHttpServer;
import de.wolfsvl.copper2go.engine.Copper2GoEngine;
import de.wolfsvl.copper2go.engine.EngineException;
import de.wolfsvl.copper2go.engine.WorkflowVersion;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class VertxHttpServer implements Copper2GoHttpServer {

    private static final Logger log = LoggerFactory.getLogger(VertxHttpServer.class);

    private final HttpServer httpServer;
    private final Vertx vertx;
    private final int port;


    public VertxHttpServer(final int port, final Copper2GoEngine copper2GoEngine) {
        this(port, copper2GoEngine, Vertx.vertx());
    }

    public VertxHttpServer(final int port, final Copper2GoEngine copper2GoEngine, final Vertx vertx) {
        this(port, copper2GoEngine, vertx, vertx.createHttpServer());
    }

    VertxHttpServer(final int port, final Copper2GoEngine copper2GoEngine, final Vertx vertx, final HttpServer httpServer) {
        this.port = port;
        this.vertx = vertx;
        this.httpServer = httpServer;
        httpServer.requestHandler(
                request -> request.bodyHandler(buffer -> {
                            final String requestBody;
                            requestBody = new String(buffer.getBytes(), StandardCharsets.UTF_8);
                            final HttpServerResponse response = request.response();
                            final String uri = request.uri();
                            if (uri.length() > 1) {
                                try {
                                    WorkflowVersion workflowVersion = WorkflowVersion.of(uri);
                                    copper2GoEngine.callWorkflow(
                                            requestBody,
                                            new HttpReplyChannelImpl(response),
                                            workflowVersion.name,
                                            workflowVersion.major,
                                            workflowVersion.minor
                                    );
                                    if (uri.contains("/event/")) {
                                        log.debug("Emtpy OK response for incoming event.");
                                        response
                                                .setStatusCode(HttpURLConnection.HTTP_ACCEPTED)
                                                .end();
                                    }
                                } catch (EngineException e) {
                                    response
                                            .setStatusCode(HttpURLConnection.HTTP_INTERNAL_ERROR)
                                            .end(String.format("Exception: %s", e.getMessage()));
                                    log.warn("Exception while calling workflow.", e);
                                }

                            } else {
                                try {
                                    response.end(Files.readString(Paths.get(getClass().getResource("/license/index.html").toURI()), StandardCharsets.UTF_8));
                                } catch (Exception e) {
                                    response.end("Exception while getting licenses." + e.getMessage());
                                }
                            }
                        }
                )
        );
    }

    @Override
    public void start() {
        log.info("Server listen on port {}", port);
        httpServer.listen(port);
    }

    @Override
    public void stop() {
        httpServer.close(e -> log.info("Server stopped. e={}", e.succeeded()));
        vertx.close();
    }

}
