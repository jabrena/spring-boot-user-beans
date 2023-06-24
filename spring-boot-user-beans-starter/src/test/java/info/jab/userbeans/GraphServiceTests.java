package info.jab.userbeans;

import static org.assertj.core.api.Assertions.assertThat;

import info.jab.support.SupportController;
import info.jab.support.TestApplication;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(
    classes = { TestApplication.class, SupportController.class },
    properties = { "management.endpoints.web.exposure.include=beans,userbeans" }
)
class GraphServiceTests {

    @Autowired
    private GraphService graphService;

    @Test
    void shouldReturnWebDocument() {
        //Given
        //When
        var html = graphService.generateWebDocument();
        Document doc = Jsoup.parse(html);

        //Then
        assertThat(doc).isNotNull();
    }

    @Test
    void shouldReturnGraphData() {
        //Given
        //When
        var resuls = graphService.generateGraphData();

        //Then
        assertThat(resuls).hasSizeGreaterThan(0);
    }

    @Test
    void shouldBePresentSpecificSupportBeans() {
        //Given
        //When
        var resuls = graphService
            .generateGraphData()
            .stream()
            .filter(edge -> edge.source().beanPackage().contains("info.jab.support"))
            .peek(System.out::println)
            .toList();

        //Then
        assertThat(resuls).hasSize(2);
    }

    @Test
    void shouldBePresentSpecificBeans() {
        //Given
        //When
        var resuls = graphService
            .generateGraphData()
            .stream()
            .filter(edge -> edge.source().beanPackage().contains("info.jab.userbeans"))
            .peek(System.out::println)
            .toList();

        //Then
        assertThat(resuls).hasSize(6);
    }
}
