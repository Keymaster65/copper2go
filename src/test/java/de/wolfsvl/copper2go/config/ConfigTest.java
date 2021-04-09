package de.wolfsvl.copper2go.config;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class ConfigTest {

    @Test
    void of() throws IOException {
        Config config = Config.of();
        assertThat(config.httpRequestChannelConfigs.size()).isEqualTo(1);
        HttpRequestChannelConfig httpRequestChannelConfig = config.httpRequestChannelConfigs.get("Pricing.centPerMinute");
        assertThat(httpRequestChannelConfig.getMethod()).isEqualTo("GET");
        assertThat(httpRequestChannelConfig.host).isEqualTo("raw.githubusercontent.com");
        assertThat(httpRequestChannelConfig.port).isEqualTo(80);
        assertThat(httpRequestChannelConfig.path).isEqualTo("/Keymaster65/copper2go-workflows/feature/1.mapping/src/workflow/resources/1.txt");
    }
}