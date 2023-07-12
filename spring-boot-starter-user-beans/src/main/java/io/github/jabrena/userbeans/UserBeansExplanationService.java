package io.github.jabrena.userbeans;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class UserBeansExplanationService {

    private static final Logger logger = LoggerFactory.getLogger(UserBeansExplanationService.class);

    private final ChatGTPProvider chatGTPProvider;
    private final WebDocumentReader webDocumentReader;

    public UserBeansExplanationService(ChatGTPProvider chatGTPProvider) {
        this.chatGTPProvider = chatGTPProvider;
        this.webDocumentReader = new WebDocumentReader();
    }

    // @formatter:off
    String generateDetailsWebDocument() {
        logger.info("Generating Web Document");
        String fileName = "static/details.html";
        return webDocumentReader.readFromResources(fileName);
    }

    // @formatter:on

    public record DetailsExplanation(String response) {}

    // @formatter:off
    public DetailsExplanation generateDetailsContent(String beanClass, String packageName, String dependency) {

        logger.info("Asking ChatGTP to explain a specific Spring Class");

        String question = """
            Can you explain in 3 lines the purpose
            of the Java class: %s
            with this package: %s
            included in this Dependency: %s ?
            """;

        question = String.format(question, beanClass, packageName, dependency);
        return new DetailsExplanation(chatGTPProvider.getAnswer(question));
    }
    // @formatter:on
}
