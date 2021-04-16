package de.wolfsvl.copper2go.application.config;

import de.wolfsvl.copper2go.connector.http.vertx.HttpRequestChannelConfig;
import io.vertx.core.http.HttpMethod;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

class ConfigTest {

    @Test
    void of() throws IOException {
        Config config = Config.of();
        assertThat(config.httpRequestChannelConfigs.size()).isEqualTo(1);
        HttpRequestChannelConfig httpRequestChannelConfig = config.httpRequestChannelConfigs.get("Pricing.centPerMinute");
        assertThat(httpRequestChannelConfig.method).isEqualTo(HttpMethod.GET);
        assertThat(httpRequestChannelConfig.host).isEqualTo("localhost");
        assertThat(httpRequestChannelConfig.port).isEqualTo(59665);
        assertThat(httpRequestChannelConfig.path).isEqualTo("/copper2go/request/1.0/Pricing");
    }
}