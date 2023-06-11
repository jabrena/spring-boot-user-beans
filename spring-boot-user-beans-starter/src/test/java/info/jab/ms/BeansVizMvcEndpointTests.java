package info.jab.ms;

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

import info.jab.ms.support.TestApplication;

@SpringBootTest(classes = TestApplication.class, webEnvironment=WebEnvironment.RANDOM_PORT)
class BeansVizMvcEndpointTests {

    @Autowired
    private TestRestTemplate restTemplate;
     
    @LocalServerPort
    int randomServerPort;

    @Test
    public void verifyThatEndpointGenerateValidJSON() throws Exception {

        final String baseUrl = "http://localhost:" + randomServerPort + "/beansviz";
        URI uri = new URI(baseUrl);
         
        ResponseEntity<String> result = this.restTemplate.getForEntity(uri, String.class);
         
        assertThat(result.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(200));

        ObjectMapper mapper = new ObjectMapper();
        var json = mapper.readTree(result.getBody());
        //TODO pending assert
    }

}