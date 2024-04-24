package com.example.javainterview;

import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ActiveProfiles("test")
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = WordCounterApplication.class
)
class ComponentTest {

    private static final String URI_FORMAT = "http://localhost:%d/%s";
    private static final String EXPECTED_HEALTH_RESPONSE_BODY = """
            {
              "status": "UP"
            }""";

    @Value("${local.server.port:8080}")
    private int port;

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.of(5L, ChronoUnit.SECONDS))
            .followRedirects(HttpClient.Redirect.NEVER)
            .build();

    @Test
    void contextLoads() throws Exception {
        HttpResponse<String> response = httpClient.send(
                HttpRequest.newBuilder(createUri("actuator/health")).GET().build(),
                HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8)
        );
        assertEquals(HttpStatus.OK.value(), response.statusCode());
        JSONAssert.assertEquals(EXPECTED_HEALTH_RESPONSE_BODY, response.body(), true);
    }

    private URI createUri(String resource) {
        return URI.create(String.format(URI_FORMAT, this.port, resource));
    }
}
