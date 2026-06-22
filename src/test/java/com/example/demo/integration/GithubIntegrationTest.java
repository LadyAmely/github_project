package com.example.demo.integration;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.web.client.HttpClientErrorException.NotFound;
import org.springframework.web.client.HttpServerErrorException.InternalServerError;
import org.springframework.web.client.RestTemplate;

import com.github.tomakehurst.wiremock.WireMockServer;

@Tag("integration")
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT, properties = { "security.enable=false" })
class GithubIntegrationTest {

    private static WireMockServer wireMock;

    private static String username;
    private static String repoJson;
    private static String emptyUsername;
    private static String nonExistentUser;

    @LocalServerPort
    private int port;

    @BeforeAll
    static void initData() {
        wireMock = new WireMockServer(options().dynamicPort());
        wireMock.start();
        username = "LadyAmely";
        repoJson = "[{\"name\":\"repo-name\",\"fork\":false,\"owner\":{\"login\":\"" + username + "\"}}]";
        nonExistentUser = "nfcuedgfcyedgcuyefdgcyuy";
        emptyUsername = " ";
    }

    @AfterAll
    static void stopWireMock() {
        wireMock.stop();
    }

    @BeforeEach
    void resetWireMock() {
        wireMock.resetAll();
    }

    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
        registry.add("github.api.url", wireMock::baseUrl);
    }

    @Test
    @DisplayName("Get user repositories returns 200 and valid response")
    void getUserRepositories_returns200() {
        wireMock.stubFor(get(urlEqualTo("/users/" + username + "/repos"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody(repoJson)
                        .withStatus(200)));

        RestTemplate restTemplate = new RestTemplate();
        String url = "http://localhost:" + port + "/api/v1/github/users/" + username + "/repositories";
        ResponseEntity<String> resp = restTemplate.getForEntity(url, String.class);

        assertEquals(HttpStatus.OK, resp.getStatusCode());
        assertNotNull(resp.getBody());
        String body = resp.getBody();
        assertTrue(body.contains("repo-name"));
        assertTrue(body.contains(username));
    }

    @Test
    @DisplayName("Get user repositories returns 404 when user does not exist")
    void getUserRepositories_returns404() {
        wireMock.stubFor(get(urlEqualTo("/users/" + nonExistentUser + "/repos"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"message\":\"User not found\",\"status\":404}")
                        .withStatus(404)));

        RestTemplate restTemplate = new RestTemplate();
        String url = "http://localhost:" + port + "/api/v1/github/users/" + nonExistentUser + "/repositories";
        try {
            restTemplate.getForEntity(url, String.class);
            throw new AssertionError("Expected 404 Not Found");
        } catch (NotFound ex) {
            assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
            assertTrue(ex.getResponseBodyAsString().contains("User not found"));
        }
    }

    @Test
    @DisplayName("Get user repositories return 500")
    void getUserRepositories_returns500() {
        wireMock.stubFor(get(urlMatching("/users/.*/repos"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"message\":\"Internal Server Error\",\"status\":500}")
                        .withStatus(500)));

        RestTemplate restTemplate = new RestTemplate();
        String url = "http://localhost:" + port + "/api/v1/github/users/" + emptyUsername + "/repositories";
        try {
            restTemplate.getForEntity(url, String.class);
            throw new AssertionError("Expected 500 Internal Server Error");
        } catch (InternalServerError ex) {
            String body = ex.getResponseBodyAsString();
            assertTrue(body.contains("\"status\":500") || body.toLowerCase().contains("internal server error"),
                    "Expected body to contain '\"status\":500' or 'Internal Server Error' but was: " + body);
        }
    }
}