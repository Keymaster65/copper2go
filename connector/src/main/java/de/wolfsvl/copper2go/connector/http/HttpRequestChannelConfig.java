package de.wolfsvl.copper2go.connector.http;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.vertx.core.http.HttpMethod;

public class HttpRequestChannelConfig {

    public final String host;
    public final int port;
    public final String path;
    public final HttpMethod method;

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public HttpRequestChannelConfig(
            @JsonProperty(required = true, value = "host") final String host,
            @JsonProperty(required = true, value = "port") final int port,
            @JsonProperty(required = true, value = "path") final String path,
            @JsonProperty(required = true, value = "method") final String method
    ) {
        this.host = host;
        this.port = port;
        this.path = path;
        this.method = HttpMethod.valueOf(method);
    }
}
