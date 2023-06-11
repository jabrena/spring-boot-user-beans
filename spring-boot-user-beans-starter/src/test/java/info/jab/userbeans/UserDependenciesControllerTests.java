package info.jab.userbeans;

import com.fasterxml.jackson.databind.ObjectMapper;
import info.jab.support.TestApplication;
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
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(
        classes = TestApplication.class,
        webEnvironment= SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserDependenciesControllerTests {

    @Autowired
    private TestRestTemplate restTemplate;

    @LocalServerPort
    int randomServerPort;

    @Test
    public void shouldGenerateDependencies() throws Exception {

        //Given
        final String baseUrl = "http://localhost:" + randomServerPort + "/api/v1/user-beans/dependencies";
        URI uri = new URI(baseUrl);

        //When
        //ResponseEntity<String[]> result = this.restTemplate.getForObject(uri, String[].class);
        ResponseEntity<List<String>> result = this.restTemplate.exchange(
                baseUrl,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                });

        //ObjectMapper mapper = new ObjectMapper();

        //List<String> jars = Arrays.asList(
        //        mapper.convertValue(result.getBody(), String[].class));

        //Then
        assertThat(result.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(200));
        assertThat(result.getBody().size()).isEqualTo(69);
    }
}
