package io.github.jabrena.userbeans;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.jabrena.support.TestApplication;
import java.net.URI;
import java.util.List;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;

@SpringBootTest(
    classes = TestApplication.class,
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = { "management.endpoints.web.exposure.include=beans,userbeans" }
)
class UserBeansEndpointsE2eTests {

    @Autowired
    private TestRestTemplate restTemplate;

    @LocalServerPort
    int randomServerPort;

    @Test
    void shouldReturnGraphWebDocument() throws Exception {
        //Given
        final String baseUrl = "http://localhost:" + randomServerPort + "/actuator/userbeans";
        URI uri = new URI(baseUrl);

        //When
        ResponseEntity<String> result = this.restTemplate.getForEntity(uri, String.class);
        Document doc = Jsoup.parse(result.getBody());

        //Then
        assertThat(result.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(200));
        assertThat(doc).isNotNull();
    }

    @Test
    void shouldGenerateTheRightData() throws Exception {
        //Given
        final String baseUrl = "http://localhost:" + randomServerPort + "/actuator/userbeans/graph";

        record BeandNode(String beanName, String beanPackage, String dependency) {}

        record ExpectedEdge(BeandNode source, BeandNode target) {}

        //When
        // @formatter:off
        ResponseEntity<List<ExpectedEdge>> result = this.restTemplate.exchange(
            baseUrl,
            HttpMethod.GET,
            null,
            new ParameterizedTypeReference<>() {});
        // @formatter:on

        //Then
        assertThat(result.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(200));
        assertThat(result.getBody()).isNotNull();
    }

    @Test
    void shouldGenerateTheRightComboData() throws Exception {
        //Given
        final String baseUrl = "http://localhost:" + randomServerPort + "/actuator/userbeans/graph-combo";

        record ExpectedDependency(String dependency) {}

        //When
        // @formatter:off
        ResponseEntity<List<ExpectedDependency>> result = this.restTemplate.exchange(
                baseUrl,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {});
        // @formatter:on

        //Then
        assertThat(result.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(200));
        assertThat(result.getBody()).isNotNull();
    }

    @Test
    void shouldReturnDetailsWebDocument() throws Exception {
        //Given
        final String baseUrl = "http://localhost:" + randomServerPort + "/actuator/userbeans/details";
        URI uri = new URI(baseUrl);

        //When
        ResponseEntity<String> result = this.restTemplate.getForEntity(uri, String.class);
        Document doc = Jsoup.parse(result.getBody());

        //Then
        assertThat(result.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(200));
        assertThat(doc).isNotNull();
    }

    @Test
    void shouldGenerateTheRightDetailsExplanationData() throws Exception {
        //Given
        String baseUrl = "http://localhost:" + randomServerPort + "/actuator/userbeans/details-explanation";
        baseUrl += "?class=x&package=y&dependency=z";
        URI uri = new URI(baseUrl);

        record ExpectedDetailsExplanation(String response) {}

        //When
        // @formatter:off
        ResponseEntity<ExpectedDetailsExplanation> result = this.restTemplate.getForEntity(uri, ExpectedDetailsExplanation.class);
        // @formatter:on

        //Then
        assertThat(result.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(200));
        assertThat(result.getBody()).isNotNull();
    }
}
