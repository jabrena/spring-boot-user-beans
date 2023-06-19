package info.jab.userbeans;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GraphService {

    Logger logger = LoggerFactory.getLogger(GraphService.class);

    @Autowired
    private UserBeansService userBeansService;

    // @formatter:off
    String generateWebDocument() {
        logger.info("Generating Web Document");
        String html = "";
        try {
            html = Files.readString(Paths.get(getClass().getClassLoader()
                    .getResource("static/graph5.html").toURI()));
        } catch (IOException | URISyntaxException e) {
            logger.warn(e.getMessage(), e);
        }
        return html;
    }

    // @formatter:on

    public record Edge(String source, String target) {}

    // @formatter:off
    List<Edge> generateGraph() {
        logger.info("Generating Graph data");
        return userBeansService.getBeansDocuments().stream()
            .flatMap(bd -> {
                String beanName = bd.beanName();
                return bd.dependencies().stream()
                        .map(dep -> new Edge(beanName, dep))
                        .toList().stream();
            })
            .toList();
    }
    // @formatter:on
}
