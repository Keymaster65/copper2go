package de.wolfsvl.copper2go.config;

import io.vertx.core.http.HttpMethod;

public class HttpRequestChannelConfig {

    private HttpMethod method;
    private String host;
    private int port;
    private String path;

    public HttpRequestChannelConfig() { }

    public String getHost() {
        return host;
    }

    public void setHost(final String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(final int port) {
        this.port = port;
    }

    public String getPath() {
        return path;
    }

    public void setPath(final String path) {
        this.path = path;
    }



    public HttpRequestChannelConfig(final HttpMethod method) {
        this.method = method;
    }

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
