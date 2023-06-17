package info.jab.userbeans;

import info.jab.support.TestApplication;
import info.jab.userbeans.UserDependenciesService.Dependency;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;

import com.fasterxml.jackson.databind.ObjectMapper;

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
    public void shouldReceiveTheListOfDependencies() throws Exception {

        //Given
        final String baseUrl = "http://localhost:" + randomServerPort + "/actuator/userbeans/dependencies";

        //When
        ResponseEntity<List<Dependency>> result = this.restTemplate.exchange(
                baseUrl,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                });

        //Then
        assertThat(result.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(200));
        assertThat(result.getBody().size()).isGreaterThan(80);
    }

    @Test
    public void shouldGenerateAJSONForTheVisualization() throws Exception {

        //Given
        final String baseUrl = "http://localhost:" + randomServerPort + "/actuator/userbeans/graph2";
        URI uri = new URI(baseUrl);

        //When
        ResponseEntity<String> result = this.restTemplate.getForEntity(uri, String.class);
        ObjectMapper mapper = new ObjectMapper();
        var expectedNotNull = mapper.readTree(result.getBody());

        //Then
        assertThat(result.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(200));
        assertThat(expectedNotNull).isNotNull();
    }
}
