package io.github.jabrena.userbeans;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import io.github.jabrena.support.TestApplication;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest(classes = { TestApplication.class }, properties = { "management.endpoints.web.exposure.include=beans,userbeans" })
class JavaClassExplanationServiceTests {

    @MockBean
    private ChatGPTProvider chatGPTProvider;

    @Autowired
    private JavaClassExplanationService beanExplanationService;

    @Test
    void shouldReturnValidDetailsWebDocument() {
        //Given
        //When
        var html = beanExplanationService.generateDetailsWebDocument();
        Document doc = Jsoup.parse(html);

        //Then
        assertThat(doc).isNotNull();
    }

    @ParameterizedTest
    @CsvSource(
        {
            "X, Y, Z, Mocked Result 1", "A, B, C, Mocked Result 2",
            // Add more test cases as needed
        }
    )
    void shouldProcessAnswerFromChatGPT(String input1, String input2, String input3, String expectedResult) {
        //Given
        //var expectedResult = "Mocked Result";
        when(chatGPTProvider.getAnswer(anyString())).thenReturn(expectedResult);

        //When
        var result = beanExplanationService.generateDetailsContent(input1, input2, input3);

        //Then
        assertThat(result.response()).isEqualTo(expectedResult);
    }
}
