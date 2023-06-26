package io.github.jabrena.userbeans;

import static com.github.stefanbirkner.systemlambda.SystemLambda.withEnvironmentVariable;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import io.github.jabrena.support.TestApplication;
import java.io.IOException;
import java.io.InputStream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest(classes = { TestApplication.class })
@TestPropertySource(properties = { "userbeans.openapi.url=http://localhost:8090/openapi" })
@EnableAutoConfiguration(exclude = { GraphService.class, UserBeansService.class, UserBeansDependencyService.class, UserBeansEndpoint.class })
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

    @Disabled
    @Test
    void shouldWorkTheIntegrationIfConfigured() throws Exception {
        //TODO it is not working
        //https://github.com/stefanbirkner/system-lambda
        withEnvironmentVariable("OPENAI_API_KEY", "XXXYYYZZZ")
            .execute(() -> {
                // @formatter:off

                //Given
                wireMockServer.stubFor(get(urlEqualTo("/openapi"))
                        .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withStatus(200)
                        .withBodyFile("chat-gtp-sample.json"))
                );

                // @formatter:on

                //When
                var response = chatGTPProvider.getAnswer("");

                //Then
                assertThat(response).isNotNull();
            });
    }

    @Test
    void shouldWorkTheIntegration() throws Exception {
        // @formatter:off

        //Given
        wireMockServer.stubFor(get(urlEqualTo("/openapi"))
                .willReturn(aResponse()
                .withHeader("Content-Type", "application/json")
                .withStatus(200)
                .withBodyFile("chat-gtp-sample.json"))
        );

        // @formatter:on

        //When
        var response = chatGTPProvider.getAnswer("");

        //Then
        assertThat(response).isNotNull();
    }

    @Test
    void shouldModelWorkProperly() throws IOException {
        //Given
        InputStream inputStream = ChatGTPProviderTests.class.getClassLoader().getResourceAsStream("__files/chat-gtp-sample.json");

        //When
        ObjectMapper objectMapper = new ObjectMapper();
        ChatGTPProvider.ChaptGTPAnswer chaptGTPAnswer = objectMapper.readValue(inputStream, ChatGTPProvider.ChaptGTPAnswer.class);

        //Then
        assertThat(chaptGTPAnswer).isNotNull();
    }
}
