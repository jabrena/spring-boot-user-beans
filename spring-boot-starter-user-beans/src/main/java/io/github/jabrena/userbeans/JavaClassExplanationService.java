package io.github.jabrena.userbeans;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class JavaClassExplanationService {

    private static final Logger logger = LoggerFactory.getLogger(JavaClassExplanationService.class);

    private final ChatGPTProvider chatGPTProvider;
    private final WebDocumentReader webDocumentReader;

    public JavaClassExplanationService(ChatGPTProvider chatGPTProvider) {
        this.chatGPTProvider = chatGPTProvider;
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

        logger.info("Asking ChatGPT to explain a specific Spring Class");

        String question = """
            Can you create an article about the purpose
            of the Java class: %s
            with this package: %s
            included in this Dependency: %s
            which include the following sections:
            - Java class purpose
            - How that Java Class interact with the rest of Spring Ecosystem?
            - What is the benefit of that Java class in Spring?
            - Is it necessary to customize in some way by the developer?

            in HTML format?
            """;

        question = String.format(question, beanClass, packageName, dependency);
        return new DetailsExplanation(chatGPTProvider.getAnswer(question));
    }
    // @formatter:on
}
