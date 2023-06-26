package io.github.jabrena.userbeans;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserBeansExplanationService {

    private static final Logger logger = LoggerFactory.getLogger(UserBeansExplanationService.class);

    @Autowired
    private ChatGTPProvider chatGTPProvider;

    // @formatter:off
    String generateDetailsWebDocument() {
        logger.info("Generating Web Document");
        String html = "";
        try {
            html = Files.readString(Paths.get(getClass().getClassLoader()
                    .getResource("static/details.html").toURI()));
        } catch (IOException | URISyntaxException e) {
            logger.warn(e.getMessage(), e);
        }
        return html;
    }

    // @formatter:on

    public record DetailsExplanation(String response) {}

    // @formatter:off
    public DetailsExplanation generateDetailsContent(String beanClass, String packageName, String dependency) {

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
