package io.github.keymaster65.copper2go.application;

import org.assertj.core.api.Assertions;

public class Assert {
    private Assert() {}

    public static void assertResponse(final String response, final String expectedPart) {
        Assertions.assertThat(response).contains(expectedPart);
    }
}
