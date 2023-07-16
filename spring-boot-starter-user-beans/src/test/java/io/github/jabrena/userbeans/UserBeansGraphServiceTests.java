package io.github.jabrena.userbeans;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.jabrena.support.SupportController;
import io.github.jabrena.support.TestApplication;
import java.util.Objects;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(
    classes = { TestApplication.class, SupportController.class },
    properties = { "management.endpoints.web.exposure.include=beans,userbeans" }
)
class UserBeansGraphServiceTests {

    @Autowired
    private UserBeansGraphService userBeansGraphService;

    @Test
    void shouldReturnValidGraphWebDocument() {
        //Given
        //When
        var html = userBeansGraphService.generateGraphWebDocument();
        Document doc = Jsoup.parse(html);

        //Then
        assertThat(doc).isNotNull();
    }

    @Test
    void shouldReturnGraphData() {
        //Given
        var noFilter = "ALL";

        //When
        var resuls = userBeansGraphService.generateGraphData(noFilter);

        //Then
        assertThat(resuls.edges()).hasSizeGreaterThan(0);
    }

    @Test
    void shouldReturnGraphDataForUnknownDependency() {
        //Given
        var filter = "UNKNOWN";

        //When
        var resuls = userBeansGraphService.generateGraphData(filter);

        //Then
        assertThat(resuls.edges()).hasSizeGreaterThan(0).hasSizeLessThan(20);
    }

    @Test
    void shouldReturnGraphDataForMicrometerCore() {
        //Given
        var filter = "micrometer-core-1.11.0.jar";

        //When
        var resuls = userBeansGraphService.generateGraphData(filter);

        //Then
        assertThat(resuls.edges()).hasSizeGreaterThan(0).hasSizeLessThan(30);
    }

    @Test
    void shouldReturnGraphDataForMicrometerObservation() {
        //Given
        var filter = "micrometer-observation-1.11.0.jar";

        //When
        var resuls = userBeansGraphService.generateGraphData(filter);

        //Then
        assertThat(resuls.edges()).hasSize(1);
    }

    @Test
    @DisplayName("Presence of false Edges")
    void shouldExistEdgesWithNoTarget() {
        //Given
        var noFilter = "ALL";

        //When
        var list = userBeansGraphService.generateGraphData(noFilter).edges().stream().filter(edge -> Objects.isNull(edge.target())).toList();

        //Then
        assertThat(list).hasSizeGreaterThan(0);
    }

    @ParameterizedTest
    @ValueSource(strings = { "io.github.jabrena.support", "io.github.jabrena.userbeans" })
    void shouldBePresentSpecificBeans(String beanPackage) {
        //Given
        //When
        var resuls = userBeansGraphService
            .generateGraphData("ALL")
            .edges()
            .stream()
            .filter(edge -> edge.source().beanPackage().contains(beanPackage))
            .toList();

        //Then
        assertThat(resuls).hasSizeGreaterThan(0);
    }
}
