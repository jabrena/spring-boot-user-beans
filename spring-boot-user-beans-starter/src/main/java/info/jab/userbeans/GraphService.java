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
    private final UserDependenciesService userDependenciesService;

    // @formatter:off
    public GraphService(
            UserBeansService userBeansService,
            UserDependenciesService userDependenciesService) {
        this.userBeansService = userBeansService;
        this.userDependenciesService = userDependenciesService;
    }

    // @formatter:on

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

    public record BeandNode(String beanName, String beanPackage, String dependency) {}

    public record Edge(BeandNode source, BeandNode target) {}

    // @formatter:off
    List<Edge> generateGraphData() {
        logger.info("Generating Graph data");

        var dependenciesAndPackages = userDependenciesService.getDependenciesAndPackages();

        return userBeansService.getBeansDocuments().stream()
            .flatMap(bd -> {
                String beanName = bd.beanName();
                String beanPackage = bd.beanPackage();
                //TODO This branch is not working well
                if (!bd.dependencies().isEmpty()) {
                    return bd.dependencies().stream()
                            .map(dep -> {
                                try {
                                    Class<?> beanClassDep = Class.forName(dep);
                                    String packageNameDependency = beanClassDep.getPackageName();

                                    return new Edge(new BeandNode(beanName, beanPackage, "PENDING"),
                                            new BeandNode(dep, packageNameDependency, "PENDING"));
                                } catch (ClassNotFoundException e) {
                                    //logger.warn("Dependency not found: {} {}",
                                    // dep, e.getMessage());

                                    record FlatDependencyPackage(
                                            String dependencyName, String packageName) {}

                                    var result = dependenciesAndPackages.stream()
                                            .flatMap(dd -> {
                                                var dependencyName = dd.dependencyName();
                                                return dd.packages().stream()
                                                        .map(str -> new FlatDependencyPackage(
                                                                dependencyName, str));
                                            })
                                            .toList();

                                    var jar = result.stream()
                                            .filter(fdp -> fdp.packageName.contains(beanPackage))
                                            .findFirst();

                                    if (jar.isPresent()) {
                                        return new Edge(new BeandNode(
                                                beanName, beanPackage, jar.get().dependencyName),
                                                new BeandNode(dep, "UNKNOWN", "UNKNOWN"));
                                    } else {
                                        return new Edge(new BeandNode(
                                                beanName, beanPackage, "UNKNOWN"),
                                                new BeandNode(dep, "UNKNOWN", "UNKNOWN"));
                                    }

                                }
                            });
                } else {
                    record FlatDependencyPackage(
                            String dependencyName, String packageName) {}

                    var result = dependenciesAndPackages.stream()
                            .flatMap(dd -> {
                                var dependencyName = dd.dependencyName();
                                return dd.packages().stream()
                                        .map(str -> new FlatDependencyPackage(
                                                dependencyName, str));
                            })
                            .toList();

                    var jar = result.stream()
                            .filter(fdp -> fdp.packageName.contains(beanPackage))
                            .findFirst();

                    if (jar.isPresent()) {
                        return Stream.of(new Edge(
                                new BeandNode(beanName, beanPackage,
                                        jar.get().dependencyName), null));
                    } else {
                        return Stream.of(new Edge(
                                new BeandNode(beanName, beanPackage, "UNKNOWN"), null));
                    }
                }
            })
            .toList();
    }
    // @formatter:on

}
