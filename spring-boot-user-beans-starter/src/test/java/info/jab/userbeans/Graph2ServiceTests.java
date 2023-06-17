package info.jab.userbeans;

import org.apache.maven.model.Dependency;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;

import info.jab.support.TestApplication;
import info.jab.userbeans.Graph2Service.DependencyCombo;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.util.List;

@SpringBootTest(
    classes = TestApplication.class,
    properties = {"management.endpoints.web.exposure.include=beans,userbeans"})
class Graph2ServiceTests {

    @Autowired
    private Graph2Service graph2Service;

    @JsonIgnoreProperties(ignoreUnknown = true)
    record GraphData(String source, String target, String value) {};

    @Test
    void shouldGenerateAllDataForTheGraph() throws Exception {

        //Given
        //When
        var rawResult = graph2Service.generateGraph2(null);
        var resuls = toGraphData(rawResult);

        //Then
        assertThat(resuls).hasSizeGreaterThan(200);
    }

    //TODO Refactor the method generateGraph2 to avoid it
    private GraphData[] toGraphData(String result) throws IOException{
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(result, GraphData[].class);
    }

    @Test
    void shouldReturnAllDataFromJars() {
        //Given
        //When
        List<DependencyCombo> result = graph2Service.generateGraph2Combo();

        result.stream().forEach(System.out::println);

        //Then
        assertThat(result).hasSize(10);
    }

    @Test
    void validateDataQuality() throws Exception {

        //Given
        var filterJar = "UNKNOWN";
        List<DependencyCombo> result = graph2Service.generateGraph2Combo();

        var unknownResults = result.stream()
            .filter(dc -> dc.dependency().equals(filterJar))
            .map(DependencyCombo::counter)
            .findFirst().get();

        //When
        String result2raw = graph2Service.generateGraph2(filterJar);
        var result2 = toGraphData(result2raw);

        //Then
        assertThat(unknownResults).isEqualTo(5);
        assertThat(result2).hasSizeGreaterThan(0);
    }
}
