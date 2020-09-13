package de.wolfsvl.copper2go;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerRequest;

import java.nio.charset.StandardCharsets;

public class Copper2GoVerticle extends AbstractVerticle {

    private final Handler<HttpServerRequest> requestHandler;
    private HttpServer httpServer = null;

    Copper2GoVerticle(Handler<HttpServerRequest> requestHandler){

        this.requestHandler = requestHandler;
    }

    @Override
    public synchronized void start(Promise<Void> promise) throws Exception {
//        super.start(promise);
        httpServer = vertx.createHttpServer();
        httpServer.requestHandler(requestHandler);
        httpServer.listen(8080);
        promise.complete();
    }

    @Override
    public void stop() throws Exception {
        httpServer.close();
        super.stop();
    }
}
