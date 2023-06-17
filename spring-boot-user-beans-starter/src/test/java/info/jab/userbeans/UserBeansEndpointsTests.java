package info.jab.userbeans;

import info.jab.support.TestApplication;
import info.jab.userbeans.Graph2Service.DependencyCombo;
import info.jab.userbeans.UserDependenciesService.Dependency;
import info.jab.userbeans.UserDependenciesService.DependencyBeanDetail;
import info.jab.userbeans.UserDependenciesService.DependencyDetail;

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

import java.net.URI;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(
        classes = TestApplication.class,
        webEnvironment= SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {
            "management.endpoints.web.exposure.include=beans,userbeans"
        })
class UserBeansEndpointsTests {

    @Autowired
    private TestRestTemplate restTemplate;

    @LocalServerPort
    int randomServerPort;

    @Test
    @DisplayName("/actuator/userbeans/beans")
    void shouldReturnBeans() throws Exception {

        //Given
        final String baseUrl = "http://localhost:" + randomServerPort + "/actuator/userbeans/beans";

        //When
        ResponseEntity<List<Dependency>> result = this.restTemplate.exchange(
                baseUrl,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {});

        //Then
        assertThat(result.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(200));
        assertThat(result.getBody()).hasSizeGreaterThan(100);
    }

    @Test
    @DisplayName("/actuator/userbeans/dependencies")
    void shouldReturnTheJarsUsedInTheApp() throws Exception {

        //Given
        final String baseUrl = "http://localhost:" + randomServerPort + "/actuator/userbeans/dependencies";

        //When
        ResponseEntity<List<Dependency>> result = this.restTemplate.exchange(
                baseUrl,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {});

        //Then
        assertThat(result.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(200));
        assertThat(result.getBody()).hasSizeGreaterThan(80);
    }

    @Test
    @DisplayName("/actuator/userbeans/dependencies/packages")
    void shouldReturnThePackagesFromJars() throws Exception {

        //Given
        final String baseUrl = "http://localhost:" + randomServerPort + "/actuator/userbeans/dependencies/packages";

        //When
        ResponseEntity<List<DependencyDetail>> result = this.restTemplate.exchange(
                baseUrl,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {});

        //Then
        assertThat(result.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(200));
        assertThat(result.getBody()).hasSizeGreaterThan(80);
    }

    @Test
    @DisplayName("/actuator/userbeans/dependencies/beans")
    void shouldReturnTheBeansFromJars() throws Exception {

        //Given
        final String baseUrl = "http://localhost:" + randomServerPort + "/actuator/userbeans/dependencies/beans";

        //When
        ResponseEntity<List<DependencyBeanDetail>> result = this.restTemplate.exchange(
                baseUrl,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {});

        //Then
        assertThat(result.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(200));
        assertThat(result.getBody()).hasSizeGreaterThan(80);
    }

    //UX

    @Test
    void shouldReturnAWebDocument() throws Exception {

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
    void shouldGenerateAJSONForTheVisualization() throws Exception {

        //Given
        final String baseUrl = "http://localhost:" + randomServerPort + "/actuator/userbeans/graph2";


        //When
        ResponseEntity<String> result = this.restTemplate.exchange(
                baseUrl,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {});

        //Then
        assertThat(result.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(200));
        assertThat(result.getBody()).isNotNull();
    }

    @Test
    void shouldReturnDataForCombo() throws Exception {

        //Given
        final String baseUrl = "http://localhost:" + randomServerPort + "/actuator/userbeans/graph2-combo";

        //When
        ResponseEntity<List<DependencyCombo>> result = this.restTemplate.exchange(
                baseUrl,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {});

        //Then
        assertThat(result.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(200));
        assertThat(result.getBody()).isNotNull();
    }
}
