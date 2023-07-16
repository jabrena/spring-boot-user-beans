package io.github.jabrena.userbeans;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
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

    @Value("${userbeans.openapi.max_tokens}")
    private Integer maxTokens;

    @Value("${userbeans.openapi.temperature}")
    private Integer temperature;

    private final String nokey = "nokey";

    @Value("${userbeans.openapi.apikey:nokey}")
    private String apiKey;

    @PostConstruct
    void after() {
        if (apiKey.equals(nokey)) {
            logger.warn("Key userbeans.openapi.apikey was not defined");
        } else {
            logger.info("Key userbeans.openapi.apikey was defined");
        }
    }

    // @formatter:off
    public record ChaptGTPAnswer(String id, String object, Integer created, String model, List<Choice> choices, Usage usage) {}

    public record Usage(Integer prompt_tokens, Integer completion_tokens, Integer total_tokens) {}

    public record Choice(String text, Integer index, Object logprobs, String finish_reason) {}

    public record RequestPayload(String model, String prompt, int max_tokens, int temperature) {}

    // @formatter:on

    String getAnswer(String question) {
        logger.info("Sending a HTTP request to ChatGTP");

        String result = "";

        try {
            HttpRequest request = prepareRequestToChatGTP(url, question, apiKey);
            HttpClient client = HttpClient.newHttpClient();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            logger.info("Processing response from ChatGTP");

            if (response.statusCode() == 200) {
                String responseBody = response.body();
                ChaptGTPAnswer answer = objectMapper.readValue(responseBody, ChaptGTPAnswer.class);
                result = answer.choices().get(0).text();
            } else {
                logger.warn("Status code: {}, Message: {}", response.statusCode(), response.body());
                result = "Something went wrong";
            }
        } catch (InterruptedException e) {
            result = "Something went wrong";
            logger.warn(e.getMessage(), e);
            Thread.currentThread().interrupt();
        } catch (IOException e) {
            result = "Something went wrong";
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
        RequestPayload payload = new RequestPayload(model, question, maxTokens, temperature);
        return objectMapper.writeValueAsString(payload);
    }
}
