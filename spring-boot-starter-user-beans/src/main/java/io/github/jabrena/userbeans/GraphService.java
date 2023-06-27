package io.github.jabrena.userbeans;

import static io.github.jabrena.userbeans.UserBeansDependencyService.UNKNOWN_DEPENDENCY;
import static io.github.jabrena.userbeans.UserBeansDependencyService.UNKNOWN_PACKAGE;

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

    private final UserBeansDependencyService userDependenciesService;

    // @formatter:off
    public GraphService(UserBeansDependencyService userDependenciesService) {
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

    public record BeanNode(String beanName, String beanPackage, String dependency) {}

    public record Edge(BeanNode source, BeanNode target) {}

    // @formatter:off
    List<Edge> generateGraphData(String dependency) {
        logger.info("Generating Graph data");

        List<UserBeansDependencyService.FlatDependencyPackage> flatDependencyPackages = userDependenciesService.getFlatDependenciPackages();

        var edges = userDependenciesService.getDependencyDocuments().stream()
            .flatMap(dd -> {
                String beanName = dd.beanName();
                String beanPackage = dd.beanPackage();
                return processDependencies(dd, beanName, beanPackage, flatDependencyPackages);
            })
            .toList();

        //TODO Remove in the future the filter. Everything will be filtered in D3.js side.
        if (Objects.isNull(dependency) || dependency.equals("ALL")) {
            return edges;
        } else {
            return edges.stream()
                    .filter(edge -> edge.source().dependency.contains(dependency))
                    .toList();
        }
    }

    // @formatter:on

    private Stream<Edge> processDependencies(
        UserBeansDependencyService.DependencyDocument dd,
        String beanName,
        String beanPackage,
        List<UserBeansDependencyService.FlatDependencyPackage> flatDependencyPackages
    ) {
        if (!dd.beanDependencies().isEmpty()) {
            return toEdgeWithDependencies(dd, beanName, beanPackage, flatDependencyPackages);
        } else {
            return toEdgeWithoutDependencies(beanName, beanPackage, flatDependencyPackages);
        }
    }

    private Stream<Edge> toEdgeWithDependencies(
        UserBeansDependencyService.DependencyDocument bd,
        String beanName,
        String beanPackage,
        List<UserBeansDependencyService.FlatDependencyPackage> flatDependencyPackages
    ) {
        return bd
            .beanDependencies()
            .stream()
            .map(dep -> {
                BeanNode sourceNode = flatDependencyPackages
                    .stream()
                    .filter(fdp -> fdp.packageName().contains(beanPackage))
                    .findFirst()
                    .map(fdp -> new BeanNode(beanName, beanPackage, fdp.dependencyName()))
                    .orElse(new BeanNode(beanName, beanPackage, UNKNOWN_DEPENDENCY));
                return new Edge(sourceNode, new BeanNode(dep, UNKNOWN_PACKAGE, UNKNOWN_DEPENDENCY));
            });
    }

    private Stream<Edge> toEdgeWithoutDependencies(
        String beanName,
        String beanPackage,
        List<UserBeansDependencyService.FlatDependencyPackage> flatDependenciPackages
    ) {
        return flatDependenciPackages
            .stream()
            .filter(fdp -> fdp.packageName().contains(beanPackage))
            .findFirst()
            .map(fdp -> Stream.of(new Edge(new BeanNode(beanName, beanPackage, fdp.dependencyName()), null)))
            .orElse(Stream.of(new Edge(new BeanNode(beanName, beanPackage, UNKNOWN_DEPENDENCY), null)));
    }

    List<UserBeansDependencyService.Dependency> generateGraphCombo() {
        return userDependenciesService.getDependencies();
    }
}
