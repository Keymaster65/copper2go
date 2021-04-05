package de.wolfsvl.copper2go.connector.http.vertx;

import de.wolfsvl.copper2go.connector.http.Copper2GoHttpClient;
import de.wolfsvl.copper2go.engine.Copper2GoEngine;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.client.HttpResponse;
import io.vertx.ext.web.client.WebClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VertxHttpClient implements Copper2GoHttpClient {

    private final String host;
    private final int port;
    private final String uri;
    private final Copper2GoEngine engine;
    private final Vertx vertx;
    private final WebClient client;
    private static final Logger log = LoggerFactory.getLogger(VertxHttpClient.class);

    public VertxHttpClient(final String host, final int port, final String uri, final Copper2GoEngine engine) {
        this.host = host;
        this.port = port;
        this.uri = uri;
        this.engine = engine;
        this.vertx = Vertx.vertx();
        this.client = WebClient.create(vertx);
    }

    private Handler<HttpResponse<Buffer>> sucesshandler(final String responseCorrelationId, final Copper2GoEngine engine) {
        return result -> {
            log.trace("Result=" + result.bodyAsString());
            engine.notify(responseCorrelationId, result.bodyAsString());
        };
    }

    private Handler<Throwable> errorHandler(final String responseCorrelationId, final Copper2GoEngine engine) {
        return err -> {
            log.trace("Failure=" + err.getMessage());
            engine.notifyError(responseCorrelationId, err.getMessage());
        };
    }

    public void request(final HttpMethod httpMethod, final String request, final String responseCorrelationId) {
        client
                .request(httpMethod, port, host, uri)
                .sendBuffer(Buffer.buffer(request))
                .onFailure(errorHandler(responseCorrelationId, engine))
                .onSuccess(sucesshandler(responseCorrelationId, engine));
    }

    @Override
    public void start() {
    }

    @Override
    public void stop() {
        client.close();
        vertx.close();
    }
}
