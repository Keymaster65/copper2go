package de.wolfsvl.copper2go.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.wolfsvl.copper2go.engine.WorkflowRepositoryConfig;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public class Config {

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private Map<String, HttpRequestChannelConfig> httpRequestChannelConfigs;
    private WorkflowRepositoryConfig workflowRepositoryConfig;

    public Config() {}

    public static Config of() throws IOException {
        String configFileName = Config.class.getResource("/de/wolfsvl/copper2go/config/config.json").getFile();
        return objectMapper.readValue(new File(configFileName), Config.class);
    }

    public Map<String, HttpRequestChannelConfig> getHttpRequestChannelConfigs() {
        return httpRequestChannelConfigs;
    }

    public WorkflowRepositoryConfig getWorkflowRepositoryConfig() {
        return workflowRepositoryConfig;
    }
}
