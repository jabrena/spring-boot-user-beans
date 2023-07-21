package io.github.jabrena;

import static org.assertj.core.api.Assertions.assertThat;

import com.pkslow.ai.AIClient;
import com.pkslow.ai.GoogleBardClient;
import com.pkslow.ai.domain.Answer;
import com.pkslow.ai.domain.AnswerStatus;
import org.junit.jupiter.api.Test;

class GoogleBardTest {

    @Test
    void shouldWorks() {
        String token = "ZAieRN7BGf6wg543LABRXAzwtc9GCEJ18b96OYBG6j-MFZZ15q5XDm31xNec5Q5lcZucGQ.";
        AIClient client = new GoogleBardClient(token);
        Answer answer = client.ask("can you show me a picture of clock?");

        System.out.println(answer.toString());
        assertThat(answer.getStatus()).isEqualTo(AnswerStatus.ERROR);
    }
}
