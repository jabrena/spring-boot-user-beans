package info.jab.d3;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URI;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;

import com.fasterxml.jackson.databind.ObjectMapper;

import info.jab.support.TestApplication;

@SpringBootTest(
        classes = TestApplication.class,
        webEnvironment=WebEnvironment.RANDOM_PORT)
class D3Graph1EndpointTests {

    @Autowired
    private TestRestTemplate restTemplate;

    @LocalServerPort
    int randomServerPort;

    @Test
    public void shouldReceiveGoodJSON() throws Exception {

        //Given
        final String baseUrl = "http://localhost:" + randomServerPort + "/graph1";
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
