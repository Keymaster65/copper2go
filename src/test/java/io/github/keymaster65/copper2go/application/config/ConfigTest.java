package io.github.keymaster65.copper2go.application.config;

import io.github.keymaster65.copper2go.connector.http.HttpMethod;
import io.github.keymaster65.copper2go.connector.http.HttpRequestChannelConfig;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

class ConfigTest {

    @Test
    void of() throws IOException {
        Config config = Config.of();
        Assertions.assertThat(config.httpRequestChannelConfigs).hasSize(1);
        HttpRequestChannelConfig httpRequestChannelConfig = config.httpRequestChannelConfigs.get("Pricing.centPerMinute");
        assertThat(httpRequestChannelConfig.method).isEqualTo(HttpMethod.GET);
        assertThat(httpRequestChannelConfig.host).isEqualTo("localhost");
        assertThat(httpRequestChannelConfig.port).isEqualTo(59665);
        assertThat(httpRequestChannelConfig.path).isEqualTo("/copper2go/request/1.0/Pricing");
    }
}