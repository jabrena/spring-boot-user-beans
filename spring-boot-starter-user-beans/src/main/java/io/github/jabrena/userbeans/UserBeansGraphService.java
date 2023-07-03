package io.github.jabrena.userbeans;

import static io.github.jabrena.userbeans.UserBeansDependencyService.UNKNOWN_DEPENDENCY;
import static io.github.jabrena.userbeans.UserBeansDependencyService.UNKNOWN_PACKAGE;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class UserBeansGraphService {

    private static final Logger logger = LoggerFactory.getLogger(UserBeansGraphService.class);

    private final UserBeansDependencyService userDependenciesService;

    // @formatter:off
    public UserBeansGraphService(UserBeansDependencyService userDependenciesService) {
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

    public record GraphData(List<BeanNode2> nodes, List<Edge2> edges) {}

    public record BeanNode(String beanName, String beanPackage, String dependency) {}

    public record BeanNode2(String beanName, String beanPackage, String dependency, Integer index) {}

    public record BeanEdge(String beanName) {}

    public record Edge(BeanNode source, BeanNode target) {}

    public record Edge2(String nodeSource, Integer source, String nodeTarget, Integer target) {}

    // @formatter:off
    List<Edge> generateGraphData(String dependency) {
        logger.info("Generating Graph data");

        List<UserBeansDependencyService.DependencyPackage> dependencyPackages = userDependenciesService.getDependencyPackages();

        var edges = userDependenciesService.getDependencyDocuments().stream()
            .flatMap(dd -> {
                String beanName = dd.beanName();
                String beanPackage = dd.beanPackage();
                return processDependencies(dd, beanName, beanPackage, dependencyPackages);
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

    public GraphData generateGraphData2(String dependency) {

        var nodes = getNodes(dependency);
        var edges = generateGraphData(dependency).stream()
                .filter(dd -> Objects.nonNull(dd.target))
                .toList();

        if (Objects.isNull(dependency) || dependency.equals("ALL")) {
            return new GraphData(getAllNodes(), edges.stream()
                    .map(dd -> {
                        try {
                            return new Edge2(
                                dd.source.beanName,
                                nodes.stream()
                                .filter(str -> str.beanName.contains(dd.source.beanName))
                                .findFirst().get().index(),
                                dd.target.beanName,
                                nodes.stream()
                                .filter(str -> str.beanName.contains(dd.target.beanName))
                                .findFirst().get().index());
                        } catch (NoSuchElementException e) {
                            return new Edge2(dd.source.beanName, 9999, dd.target.beanName, 9999);
                        }
                    })
                    .toList());
        } else {
            return new GraphData(nodes, edges.stream()
                    .filter(edge -> edge.source().dependency.contains(dependency)
                            || edge.target().dependency.contains(dependency))
                    .map(dd -> {
                        try {
                            return new Edge2(
                                dd.source.beanName,
                                nodes.stream()
                                        .filter(str -> str.beanName.contains(dd.source.beanName))
                                        .findFirst().get().index(),
                                dd.target.beanName,
                                nodes.stream()
                                        .filter(str -> str.beanName.contains(dd.target.beanName))
                                        .findFirst().get().index());
                        } catch (NoSuchElementException e) {
                            return new Edge2(dd.source.beanName, 9999, dd.target.beanName, 9999);
                        }
                    }).toList());
        }
    }

    private List<BeanNode2> getNodes(String dependency) {
        return this.getAllNodes().stream()
                .filter(b -> b.dependency.equals(dependency))
                .distinct()
                .toList();
    }

    private List<BeanNode2> getAllNodes() {
        AtomicInteger counter = new AtomicInteger(0);
        return userDependenciesService.getDependencyDocuments().stream()
                .flatMap(dd -> {
                    List<BeanNode2> list = new ArrayList<>();
                    list.add(new BeanNode2(dd.beanName(), dd.beanPackage(), dd.dependency(), null));
                    dd.beanDependencies().forEach(dep -> {
                            list.add(new BeanNode2(dep, UNKNOWN_PACKAGE, UNKNOWN_DEPENDENCY, null));
                    });
                    return list.stream();
                })
                .distinct()
                .map(bn -> new BeanNode2(bn.beanName, bn.beanPackage, bn.dependency, counter.incrementAndGet()))
                .sorted(Comparator.comparing(BeanNode2::beanName))
                .toList();
    }

    // @formatter:on

    private Stream<Edge> processDependencies(
        UserBeansDependencyService.DependencyDocument dd,
        String beanName,
        String beanPackage,
        List<UserBeansDependencyService.DependencyPackage> dependencyPackages
    ) {
        if (!dd.beanDependencies().isEmpty()) {
            return toEdgeWithDependencies(dd, beanName, beanPackage, dependencyPackages);
        } else {
            return toEdgeWithoutDependencies(beanName, beanPackage, dependencyPackages);
        }
    }

    private Stream<Edge> toEdgeWithDependencies(
        UserBeansDependencyService.DependencyDocument bd,
        String beanName,
        String beanPackage,
        List<UserBeansDependencyService.DependencyPackage> dependencyPackages
    ) {
        return bd
            .beanDependencies()
            .stream()
            .map(dep -> {
                BeanNode sourceNode = dependencyPackages
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
        List<UserBeansDependencyService.DependencyPackage> flatDependenciPackages
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
