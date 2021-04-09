package de.wolfsvl.copper2go.application.config;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public class Config {

    public Map<String, HttpRequestChannelConfig> httpRequestChannelConfigs;
    public WorkflowRepositoryConfig workflowRepositoryConfig;

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public Config() {}

    public static Config of() throws IOException {
        String configFileName = Config.class.getResource("/de/wolfsvl/copper2go/application/config/config.json").getFile();
        return objectMapper.readValue(new File(configFileName), Config.class);
    }
}
