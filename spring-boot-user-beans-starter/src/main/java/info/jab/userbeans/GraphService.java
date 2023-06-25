package info.jab.userbeans;

import static info.jab.userbeans.UserDependenciesService.UNKNOWN_DEPENDENCY;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class GraphService {

    private static final Logger logger = LoggerFactory.getLogger(GraphService.class);

    private final UserDependenciesService userDependenciesService;

    // @formatter:off
    public GraphService(UserDependenciesService userDependenciesService) {
        this.userDependenciesService = userDependenciesService;
    }

    // @formatter:on

    // @formatter:off
    String generateGraphWebDocument() {
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

    //TODO Review to refactor the method using small parts
    // @formatter:off
    List<Edge> generateGraphData(String dependency) {
        logger.info("Generating Graph data");
        logger.info(dependency);

        if (Objects.nonNull(dependency)) {
            if (dependency.equals("ALL")) {
                dependency = null;
            }
        }

        var dependenciesAndPackages = userDependenciesService.getDependenciesAndPackages();

        record FlatDependencyPackage(String dependencyName, String packageName) {}

        var result = dependenciesAndPackages.stream()
                .flatMap(dd -> {
                    var dependencyName = dd.dependencyName();
                    return dd.packages().stream().map(str -> new FlatDependencyPackage(dependencyName, str));
                })
                .toList();

        var result2 = userDependenciesService.getBeansDocuments().stream()
            .flatMap(bd -> {
                String beanName = bd.beanName();
                String beanPackage = bd.beanPackage();
                if (!bd.dependencies().isEmpty()) {
                    return bd.dependencies().stream()
                            .map(dep -> {
                                //TODO This branch is not working well
                                try {
                                    Class<?> beanClassDep = Class.forName(dep);
                                    String packageNameDependency = beanClassDep.getPackageName();

                                    return new Edge(
                                            new BeandNode(beanName, beanPackage, "PENDING"),
                                            new BeandNode(dep, packageNameDependency, "PENDING"));
                                } catch (ClassNotFoundException e) {
                                    var jar = result.stream()
                                            .filter(fdp -> fdp.packageName.contains(beanPackage))
                                            .findFirst();

                                    if (jar.isPresent()) {
                                        return new Edge(
                                                new BeandNode(beanName, beanPackage, jar.get().dependencyName),
                                                new BeandNode(dep, "UNKNOWN", UNKNOWN_DEPENDENCY));
                                    } else {
                                        return new Edge(
                                                new BeandNode(beanName, beanPackage, UNKNOWN_DEPENDENCY),
                                                new BeandNode(dep, "UNKNOWN", UNKNOWN_DEPENDENCY));
                                    }

                                }
                            });
                } else {
                    var jar = result.stream()
                            .filter(fdp -> fdp.packageName.contains(beanPackage))
                            .findFirst();

                    if (jar.isPresent()) {
                        return Stream.of(new Edge(new BeandNode(beanName, beanPackage, jar.get().dependencyName), null));
                    } else {
                        return Stream.of(new Edge(new BeandNode(beanName, beanPackage, UNKNOWN_DEPENDENCY), null));
                    }
                }
            })
            .toList();

        if (Objects.isNull(dependency)) {
            return result2;
        } else {
            String finalDependency = dependency;
            return result2.stream()
                    .filter(edge -> edge.source().dependency.contains(finalDependency))
                    .toList();
        }
    }

    // @formatter:on

    List<UserDependenciesService.Dependency> generateGraphCombo() {
        return userDependenciesService.getDependencies();
    }
}
