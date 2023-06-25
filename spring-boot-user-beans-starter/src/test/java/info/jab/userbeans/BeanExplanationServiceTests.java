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
class BeanExplanationServiceTests {

    @Autowired
    private BeanExplanationService beanExplanationService;

    @Test
    void shouldReturnValidDetailsWebDocument() {
        //Given
        //When
        var html = beanExplanationService.generateDetailsWebDocument();
        Document doc = Jsoup.parse(html);

        //Then
        assertThat(doc).isNotNull();
    }
}
