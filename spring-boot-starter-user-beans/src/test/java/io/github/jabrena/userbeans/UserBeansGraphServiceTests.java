package io.github.jabrena.userbeans;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.jabrena.support.SupportController;
import io.github.jabrena.support.TestApplication;
import java.util.Objects;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
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
        //When
        var resuls = userBeansGraphService.generateGraphData("ALL");

        //Then
        assertThat(resuls).hasSizeGreaterThan(0);
    }

    @Test
    @DisplayName("Presence of false Edges")
    void shouldExistEdgesWithNoTarget() {
        //Given
        var noFilter = "ALL";

        //When
        var list = userBeansGraphService.generateGraphData(noFilter).stream().filter(edge -> Objects.isNull(edge.target())).toList();

        //Then
        assertThat(list).hasSizeGreaterThan(0);
    }

    @Test
    void shouldBePresentSpecificSupportBeans() {
        //Given
        //When
        var resuls = userBeansGraphService
            .generateGraphData("ALL")
            .stream()
            .filter(edge -> edge.source().beanPackage().contains("io.github.jabrena.support"))
            .peek(System.out::println)
            .toList();

        //Then
        assertThat(resuls).hasSize(2);
    }

    @Test
    void shouldBePresentSpecificBeans() {
        //Given
        //When
        var resuls = userBeansGraphService
            .generateGraphData("ALL")
            .stream()
            .filter(edge -> edge.source().beanPackage().contains("io.github.jabrena.userbeans"))
            .peek(System.out::println)
            .toList();

        //Then
        assertThat(resuls).hasSize(8);
    }
}
