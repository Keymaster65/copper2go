package io.github.keymaster65.copper2go.engine.vanilla;

import java.util.function.Consumer;

record Continuation(
        Consumer<String> consumer,
        String response
) {
    Continuation(String response) {
        this(null, response);
    }

    Continuation(Consumer<String> consumer) {
        this(consumer, null);
    }
}
