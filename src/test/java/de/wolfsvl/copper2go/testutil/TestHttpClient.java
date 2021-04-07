package de.wolfsvl.copper2go.testutil;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

import static de.wolfsvl.copper2go.application.Application.HTTP_SERVER_PORT;

public class TestHttpClient {
    public static HttpResponse<String> post(final String name) throws java.io.IOException, InterruptedException {
        HttpClient client = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .followRedirects(HttpClient.Redirect.NORMAL)
                .connectTimeout(Duration.ofSeconds(20))
                .build();
        HttpRequest httpRequest =
                HttpRequest.newBuilder()
                        .timeout(Duration.ofMillis(3000))
                        .uri(URI.create("http://localhost:" + HTTP_SERVER_PORT))
                        .POST(HttpRequest.BodyPublishers.ofString(name))
                        .build();
        return client.send(httpRequest, HttpResponse.BodyHandlers.ofString());
    }
}
