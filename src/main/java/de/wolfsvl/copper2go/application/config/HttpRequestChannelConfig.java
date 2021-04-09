package de.wolfsvl.copper2go.application.config;

import io.vertx.core.http.HttpMethod;

public class HttpRequestChannelConfig {

    public String host;
    public int port;
    public String path;
    private HttpMethod method;

    public HttpRequestChannelConfig() { }

    public void setMethod(final String method) {
        this.method = HttpMethod.valueOf(method);
    }

    public String getMethod() {
        return method.name();
    }

    public HttpMethod getHttpMethod() {
        return method;
    }
}
