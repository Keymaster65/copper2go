package io.github.keymaster65.copper2go.connector.http.vertx;

import io.github.keymaster65.copper2go.connector.http.Copper2GoHttpClient;
import io.github.keymaster65.copper2go.connector.http.HttpMethod;
import io.github.keymaster65.copper2go.engine.Copper2GoEngine;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
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
            if (log.isTraceEnabled()) {
                log.trace(String.format("Result=%s", result.bodyAsString()));
            }
            engine.notify(responseCorrelationId, result.bodyAsString());
        };
    }

    private Handler<Throwable> errorHandler(final String responseCorrelationId, final Copper2GoEngine engine) {
        return err -> {
            if (log.isTraceEnabled()) {
                log.trace(String.format("Failure=%s", err.getMessage()));
            }
            engine.notifyError(responseCorrelationId, err.getMessage());
        };
    }

    public void request(final HttpMethod httpMethod, final String request, final String responseCorrelationId) {
        client
                .request(io.vertx.core.http.HttpMethod.valueOf(httpMethod.toString()), port, host, uri)
                .sendBuffer(Buffer.buffer(request))
                .onFailure(errorHandler(responseCorrelationId, engine))
                .onSuccess(sucesshandler(responseCorrelationId, engine));
    }

    @Override
    public void close() {
        client.close();
        vertx.close();
    }
}
