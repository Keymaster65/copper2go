package de.wolfsvl.copper2go.application.config;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.wolfsvl.copper2go.connector.http.HttpRequestChannelConfig;
import de.wolfsvl.copper2go.engine.WorkflowRepositoryConfig;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class Config {

    public final Map<String, HttpRequestChannelConfig> httpRequestChannelConfigs;
    public final WorkflowRepositoryConfig workflowRepositoryConfig;
    public final int maxTickets;
    public final int httpPort;

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public Config(
            @JsonProperty(value = "httpRequestChannelConfigs") final Map<String, HttpRequestChannelConfig> httpRequestChannelConfigs,
            @JsonProperty(required = true, value = "workflowRepositoryConfig") final WorkflowRepositoryConfig workflowRepositoryConfig,
            @JsonProperty(required = true, value = "maxTickets") final int maxTickets,
            @JsonProperty(required = true, value = "httpPort") final int httpPort
    ) {
        this.httpRequestChannelConfigs = httpRequestChannelConfigs;
        this.workflowRepositoryConfig = workflowRepositoryConfig;
        this.maxTickets = maxTickets;
        this.httpPort = httpPort;
    }

    public static Config of() throws IOException {

        return objectMapper.readValue(
                new InputStreamReader(Config.class.getResourceAsStream("/de/wolfsvl/copper2go/application/config/config.json"), StandardCharsets.UTF_8),
                Config.class);
    }

    public static Config of(final String config) throws IOException {
        return objectMapper.readValue(config, Config.class);
    }
}
