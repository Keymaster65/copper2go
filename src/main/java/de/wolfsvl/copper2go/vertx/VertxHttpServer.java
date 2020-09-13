package de.wolfsvl.copper2go.vertx;

import de.wolfsvl.copper2go.application.Application;
import de.wolfsvl.copper2go.application.Copper2GoHttpServer;
import de.wolfsvl.copper2go.impl.HttpContextImpl;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;

public class VertxHttpServer implements Copper2GoHttpServer {

    private static final Logger log = LoggerFactory.getLogger(Application.class);

    private final HttpServer httpServer;
    private final Vertx vertx;
    private final int port;
    private final Application application;


    public VertxHttpServer(final int port, final Application application) {
        this(port, application, Vertx.vertx());
    }

    VertxHttpServer(final int port, final Application application, final Vertx vertx) {
        this(port, application, vertx, vertx.createHttpServer());
    }
    VertxHttpServer(final int port, final Application application, final Vertx vertx, final HttpServer httpServer) {
        this.port = port;
        this.application = application;
        this.vertx = vertx;
        this.httpServer = httpServer;
        httpServer.requestHandler(new Handler<HttpServerRequest>() {
            @Override
            public void handle(HttpServerRequest request) {
                request.handler(new Handler<Buffer>() {
                    @Override
                    public void handle(Buffer buffer) {

                        final String requestBody = new String(buffer.getBytes(), StandardCharsets.UTF_8);
                        try {
                            application.callWorkflow(new HttpContextImpl(requestBody, request.response()));
                        } catch (Exception e) {
                            request.response().end("Expection: " + e.getMessage());
                            log.warn("Exception while calling workflow.", e);
                        }
                    }
                });
            }
        });
    }


    public void start() {
        httpServer.listen(port);
    }

    public void stop() {
        httpServer.close(e -> System.out.println("e=" + e.succeeded()));
        vertx.close();
    }

}
