package io.github.keymaster65.copper2go.connector.http.vertx;

import io.vertx.core.MultiMap;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import static java.util.Map.entry;

class RequestHandlerTest {

    @Test
    void createAttributes() {
        MultiMap multiMap = MultiMap.caseInsensitiveMultiMap();
        multiMap.add("a", "A");
        Assertions.assertThat(RequestHandler.createAttributes(multiMap))
                .containsOnly(entry("a", "A"))
                .hasSize(1);
    }

    @Test
    void createAttributesDouble() {
        MultiMap multiMap = MultiMap.caseInsensitiveMultiMap();
        multiMap.add("a", "A");
        multiMap.add("a", "A");
        Assertions.assertThat(RequestHandler.createAttributes(multiMap))
                .containsOnly(entry("a", "A"))
                .hasSize(1);
    }

    @Test
    void createAttributesEmpty() {
        MultiMap multiMap = MultiMap.caseInsensitiveMultiMap();
        Assertions.assertThat(RequestHandler.createAttributes(multiMap)).isNull();
    }

    @Test
    void createAttributesNull() {
        Assertions.assertThat(RequestHandler.createAttributes(null)).isNull();
    }
}