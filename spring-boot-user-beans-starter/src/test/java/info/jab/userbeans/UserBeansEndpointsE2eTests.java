package info.jab.userbeans;

import static org.assertj.core.api.Assertions.assertThat;

import info.jab.support.TestApplication;
import info.jab.userbeans.UserDependenciesService.Dependency;
import info.jab.userbeans.UserDependenciesService.DependencyBeanDetail;
import java.net.URI;
import java.util.List;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.DisplayName;
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
    @DisplayName("/actuator/userbeans/beans")
    void shouldReturnTheUserBeans() throws Exception {
        //Given
        final String baseUrl = "http://localhost:" + randomServerPort + "/actuator/userbeans/beans";

        record ExpectedBeanDocument(String name, String packageName, List<String> dependencies) {}

        //When
        // @formatter:off
        ResponseEntity<List<ExpectedBeanDocument>> result = this.restTemplate.exchange(
            baseUrl,
            HttpMethod.GET,
            null,
            new ParameterizedTypeReference<>() {});
        // @formatter:on

        //Then
        assertThat(result.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(200));
        assertThat(result.getBody()).hasSizeGreaterThan(0);
    }

    @Test
    @DisplayName("/actuator/userbeans/dependencies")
    void shouldReturnTheJarsUsedInTheApp() throws Exception {
        //Given
        final String baseUrl = "http://localhost:" + randomServerPort + "/actuator/userbeans/dependencies";

        //When
        // @formatter:off
        ResponseEntity<List<Dependency>> result = this.restTemplate.exchange(
            baseUrl,
            HttpMethod.GET,
            null,
            new ParameterizedTypeReference<>() {});
        // @formatter:on

        //Then
        assertThat(result.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(200));
        assertThat(result.getBody()).hasSizeGreaterThan(0);
    }

    @Test
    @DisplayName("/actuator/userbeans/dependencies/beans")
    void shouldReturnTheBeansFromJars() throws Exception {
        //Given
        final String baseUrl = "http://localhost:" + randomServerPort + "/actuator/userbeans/dependencies/beans";

        //When
        // @formatter:off
        ResponseEntity<List<DependencyBeanDetail>> result = this.restTemplate.exchange(
            baseUrl,
            HttpMethod.GET,
            null,
            new ParameterizedTypeReference<>() {});
        // @formatter:on

        //Then
        assertThat(result.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(200));
        assertThat(result.getBody()).hasSizeGreaterThan(0);
    }

    //UX

    @Test
    void shouldReturnWebDocument() throws Exception {
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

        record BeandNode(String beanName, String beanPackage) {}

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
}
