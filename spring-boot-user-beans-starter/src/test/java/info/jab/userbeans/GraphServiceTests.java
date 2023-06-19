package info.jab.userbeans;

import static org.assertj.core.api.Assertions.assertThat;

import info.jab.support.TestApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(
    classes = TestApplication.class,
    properties = { "management.endpoints.web.exposure.include=beans,userbeans" }
)
class GraphServiceTests {

    @Autowired
    private GraphService graphService;

    record GraphData(String source, String target) {}

    @Test
    void shouldGenerateAllDataForTheGraph() throws Exception {
        //Given
        //When
        var resuls = graphService.generateGraph();

        //Then
        assertThat(resuls).hasSizeGreaterThan(200);
    }
}
