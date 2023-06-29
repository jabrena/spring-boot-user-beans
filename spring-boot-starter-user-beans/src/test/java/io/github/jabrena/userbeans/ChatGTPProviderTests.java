package io.github.jabrena.userbeans;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.assertj.core.api.Assertions.assertThat;

import com.github.tomakehurst.wiremock.WireMockServer;
import io.github.jabrena.support.TestApplication;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest(classes = { TestApplication.class })
@TestPropertySource(properties = { "userbeans.openapi.url=http://localhost:8090/openapi", "userbeans.openapi.apikey=XXXYYYZZZ" })
@EnableAutoConfiguration(exclude = { UserBeansGraphService.class, UserBeansService.class, UserBeansDependencyService.class, UserBeansEndpoint.class })
class ChatGTPProviderTests {

    @Autowired
    private ChatGTPProvider chatGTPProvider;

    WireMockServer wireMockServer;

    @BeforeEach
    public void setup() {
        wireMockServer = new WireMockServer(8090);
        wireMockServer.start();
    }

    @AfterEach
    public void teardown() {
        wireMockServer.stop();
    }

    @Test
    void shouldWorkTheIntegrationIfConfigured() throws Exception {
        // @formatter:off

        //Given
        wireMockServer.stubFor(post(urlEqualTo("/openapi"))
                .willReturn(aResponse()
                .withHeader("Content-Type", "application/json")
                .withStatus(200)
                .withBodyFile("200-ok.json"))
        );

        // @formatter:on

        //When
        var response = chatGTPProvider.getAnswer("");

        //Then
        assertThat(response).isNotNull().as("Response should not be null");
    }

    @Test
    void shouldHandleBadKeyScenario() throws Exception {
        // @formatter:off

        //Given
        wireMockServer.stubFor(post(urlEqualTo("/openapi"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withStatus(200)
                        .withBodyFile("401-incorrect-api-key.json"))
        );

        // @formatter:on

        //When
        var response = chatGTPProvider.getAnswer("");

        //Then
        assertThat(response).isEqualTo("Something went wrong").as("Response should be 'Something went wrong'");
    }

    @Test
    void shouldWorkTheIntegration() throws Exception {
        // @formatter:off

        //Given
        wireMockServer.stubFor(post(urlEqualTo("/openapi"))
                .willReturn(aResponse()
                .withHeader("Content-Type", "application/json")
                .withStatus(200)
                .withBodyFile("200-ok.json"))
        );

        // @formatter:on

        //When
        var response = chatGTPProvider.getAnswer("");

        //Then
        assertThat(response).isNotEmpty().as("Response should not be empty");
    }
}
