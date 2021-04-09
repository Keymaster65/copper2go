package de.wolfsvl.copper2go.application.config;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public class Config {

    public final Map<String, HttpRequestChannelConfig> httpRequestChannelConfigs;
    public final WorkflowRepositoryConfig workflowRepositoryConfig;

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public Config(
            @JsonProperty(value = "httpRequestChannelConfigs") final Map<String, HttpRequestChannelConfig> httpRequestChannelConfigs,
            @JsonProperty(required = true, value = "workflowRepositoryConfig") final WorkflowRepositoryConfig workflowRepositoryConfig
    ) {
        this.httpRequestChannelConfigs = httpRequestChannelConfigs;
        this.workflowRepositoryConfig = workflowRepositoryConfig;
    }

    public static Config of() throws IOException {
        String configFileName = Config.class.getResource("/de/wolfsvl/copper2go/application/config/config.json").getFile();
        return objectMapper.readValue(new File(configFileName), Config.class);
    }
}
