package info.jab.ms;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = { "management.endpoints.web.exposure.include=beans,userbeans" }
)
class MainApplicationE2eTests {

    @Autowired
    private TestRestTemplate restTemplate;

    @LocalServerPort
    int randomServerPort;

    @Test
    void shouldReturnTheUserBeans() throws Exception {
        //Given
        final String baseUrl = "http://localhost:" + randomServerPort + "/demo";

        record ExpectedHelloWorld(String message) {}

        //When
        // @formatter:off
        ResponseEntity<ExpectedHelloWorld> result = this.restTemplate.exchange(
            baseUrl,
            HttpMethod.GET,
            null,
            ExpectedHelloWorld.class);
        // @formatter:on

        //Then
        assertThat(result.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(200));
        assertThat(result.getBody()).isNotNull();
    }
}
