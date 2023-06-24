package info.jab.userbeans;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class GraphService {

    Logger logger = LoggerFactory.getLogger(GraphService.class);

    private final UserBeansService userBeansService;

    public GraphService(UserBeansService userBeansService) {
        this.userBeansService = userBeansService;
    }

    // @formatter:off
    String generateWebDocument() {
        logger.info("Generating Web Document");
        String html = "";
        try {
            html = Files.readString(Paths.get(getClass().getClassLoader()
                    .getResource("static/graph.html").toURI()));
        } catch (IOException | URISyntaxException e) {
            logger.warn(e.getMessage(), e);
        }
        return html;
    }

    // @formatter:on

    public record BeandNode(String beanName, String beanPackage) {}

    public record Edge(BeandNode source, BeandNode target) {}

    // @formatter:off
    List<Edge> generateGraphData() {
        logger.info("Generating Graph data");
        return userBeansService.getBeansDocuments().stream()
            .flatMap(bd -> {
                String beanName = bd.beanName();
                String beanPackage = bd.beanPackage();
                if (!bd.dependencies().isEmpty()) {
                    return bd.dependencies().stream()
                            .map(dep -> {
                                try {
                                    Class<?> beanClassDep = Class.forName(dep);
                                    String packageNameDependency = beanClassDep.getPackageName();
                                    return new Edge(new BeandNode(beanName, beanPackage),
                                            new BeandNode(dep, packageNameDependency));
                                } catch (ClassNotFoundException e) {
                                    logger.warn("Dependency not found: {} {}", dep, e.getMessage());
                                    return new Edge(new BeandNode(beanName, beanPackage),
                                            new BeandNode(dep, "UNKNOWN"));
                                }
                            });
                } else {
                    return Stream.of(new Edge(new BeandNode(beanName, beanPackage), null));
                }
            })
            .toList();
    }
    // @formatter:on
}
