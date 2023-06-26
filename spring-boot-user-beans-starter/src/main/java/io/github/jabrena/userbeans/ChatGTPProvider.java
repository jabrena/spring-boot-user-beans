package io.github.jabrena.userbeans;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

@Service
@PropertySource("classpath:/io/github/jabrena/userbeans/application.properties")
public class ChatGTPProvider {

    private static final Logger logger = LoggerFactory.getLogger(ChatGTPProvider.class);

    @Qualifier("ChatGTPMapper")
    @Autowired
    private ObjectMapper objectMapper;

    @Value("${userbeans.openapi.url}")
    private String url;

    @Value("${userbeans.openapi.model}")
    private String model;

    // @formatter:off
    public record ChaptGTPAnswer(String id, String object, Integer created, String model, List<Choice> choices, Usage usage) {}

    public record Usage(Integer prompt_tokens, Integer completion_tokens, Integer total_tokens) {}

    public record Choice(String text, Integer index, Object logprobs, String finish_reason) {}

    public record RequestPayload(String model, String prompt, int max_tokens, int temperature) {}

    // @formatter:on

    String getAnswer(String question) {
        logger.info("Sending a question to ChatGTP");

        String key = System.getenv().get("OPENAI_API_KEY");

        if (Objects.isNull(key)) {
            return """
            Sorry, something went wrong.
            Check if OPENAI_API_KEY variable was defined in your environment
            to enable this feature.
            """;
        }

        String result = "";

        try {
            HttpRequest request = prepareRequestToChatGTP(url, question, key);
            HttpClient client = HttpClient.newHttpClient();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            logger.info("Processing response from ChatGTP");

            if (response.statusCode() == 200) {
                String responseBody = response.body();
                ChaptGTPAnswer answer = objectMapper.readValue(responseBody, ChaptGTPAnswer.class);
                result = answer.choices().get(0).text();
            } else {
                result = "Something went wrong";
            }
        } catch (InterruptedException e) {
            logger.warn(e.getMessage(), e);
            Thread.currentThread().interrupt();
        } catch (IOException e) {
            logger.warn(e.getMessage(), e);
        }
        return result;
    }

    private HttpRequest prepareRequestToChatGTP(String url, String question, String key) throws JsonProcessingException {
        return HttpRequest
            .newBuilder()
            .uri(URI.create(url))
            .headers(getHeaders(key))
            .POST(HttpRequest.BodyPublishers.ofString(getBodyPayload(question)))
            .build();
    }

    // @formatter:off

    private String[] getHeaders(String key) {
        return List.of(
                "Content-Type", "application/json",
                "Accept", "application/json",
                "Authorization", "Bearer " + key)
            .toArray(String[]::new);
    }

    // @formatter:on

    private String getBodyPayload(String question) throws JsonProcessingException {
        RequestPayload payload = new RequestPayload(model, question, 4000, 0);
        return objectMapper.writeValueAsString(payload);
    }
}
