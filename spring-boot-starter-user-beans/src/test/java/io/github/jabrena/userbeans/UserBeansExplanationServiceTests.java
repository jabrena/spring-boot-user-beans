package io.github.jabrena.userbeans;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import io.github.jabrena.support.TestApplication;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest(classes = { TestApplication.class }, properties = { "management.endpoints.web.exposure.include=beans,userbeans" })
class UserBeansExplanationServiceTests {

    @MockBean
    private ChatGTPProvider chatGTPProvider;

    @Autowired
    private UserBeansExplanationService beanExplanationService;

    @Test
    void shouldReturnValidDetailsWebDocument() {
        //Given
        //When
        var html = beanExplanationService.generateDetailsWebDocument();
        Document doc = Jsoup.parse(html);

        //Then
        assertThat(doc).isNotNull();
    }

    @Test
    void shouldProcessAnswerFromChatGTP() {
        //Given
        var expectedResult = "Mocked Result";
        when(chatGTPProvider.getAnswer(anyString())).thenReturn(expectedResult);

        //When
        var result = beanExplanationService.generateDetailsContent("X", "Y", "Z");

        //Then
        assertThat(result.response()).isEqualTo(expectedResult);
    }
}
