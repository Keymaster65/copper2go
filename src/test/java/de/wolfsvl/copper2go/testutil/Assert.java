package de.wolfsvl.copper2go.testutil;

import org.assertj.core.api.Assertions;

public class Assert {

    public static void assertResponse(final String response, final String expectedPart) {
        Assertions.assertThat(response).contains(expectedPart);
    }
}
