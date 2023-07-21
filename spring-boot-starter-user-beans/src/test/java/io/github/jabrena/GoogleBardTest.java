package io.github.jabrena;

import static org.assertj.core.api.Assertions.assertThat;

import com.pkslow.ai.Client;
import com.pkslow.ai.domain.Answer;
import com.pkslow.ai.domain.AnswerStatus;
import org.junit.jupiter.api.Test;

class GoogleBardTest {

    @Test
    void shouldWorks() {
        String token = "";
        AIClient client = new GoogleBardClient(token);
        Answer answer = client.ask("can you show me a picture of clock?");

        assertThat(answer.getStatus()).isEqualTo(AnswerStatus.OK);
    }
}
