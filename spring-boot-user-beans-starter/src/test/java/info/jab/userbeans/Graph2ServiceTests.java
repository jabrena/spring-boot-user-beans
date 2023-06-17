package info.jab.userbeans;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import info.jab.support.TestApplication;
import info.jab.userbeans.UserBeansEndpoint.DependencyCombo;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

@SpringBootTest(
    classes = TestApplication.class,
    properties = {"management.endpoints.web.exposure.include=beans,userbeans"})
public class Graph2ServiceTests {

    @Autowired
    private Graph2Service graph2Service;

    @JsonIgnoreProperties(ignoreUnknown = true)
    record GraphData(String source, String target, String value) {};

    @Test
    void shouldGenerateAllDataForTheGraph() throws Exception{

        //Given
        //When
        var rawResult = graph2Service.generateGraph2(null).getBody();

        ObjectMapper objectMapper = new ObjectMapper();
        GraphData[] myObjects;
        myObjects = objectMapper.readValue(rawResult, GraphData[].class);

        //Then
        assertThat(myObjects.length).isGreaterThan(200);
    }

    @Test
    void shouldReturnAllDataFromJars() {
        //Given
        //When
        List<DependencyCombo> result = graph2Service.generateGraph2Combo().getBody();

        result.stream().forEach(System.out::println);

        //Then
        assertThat(result.size()).isEqualTo(10);
    }
}
