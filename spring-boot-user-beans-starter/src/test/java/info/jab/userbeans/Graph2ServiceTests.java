package info.jab.userbeans;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import info.jab.support.TestApplication;
import info.jab.userbeans.Graph2Service.DependencyCombo;
import info.jab.userbeans.Graph2Service.EdgeOutput;

import static org.assertj.core.api.Assertions.assertThat;

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
        var resuls = graph2Service.generateGraph2(null);
        //var resuls = toGraphData(rawResult);

        //Then
        assertThat(resuls).hasSizeGreaterThan(200);
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
        List<EdgeOutput> result2 = graph2Service.generateGraph2(filterJar);

        //Then
        assertThat(unknownResults).isEqualTo(5);
        assertThat(result2).hasSizeGreaterThan(0);
    }
}
