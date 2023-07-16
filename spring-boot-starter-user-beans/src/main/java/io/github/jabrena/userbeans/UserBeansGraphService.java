package io.github.jabrena.userbeans;

import static io.github.jabrena.userbeans.UserBeansDependencyService.UNKNOWN_DEPENDENCY;
import static io.github.jabrena.userbeans.UserBeansDependencyService.UNKNOWN_PACKAGE;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class UserBeansGraphService {

    private static final Logger logger = LoggerFactory.getLogger(UserBeansGraphService.class);

    private final UserBeansDependencyService userDependenciesService;
    private final WebDocumentReader webDocumentReader;

    // @formatter:off
    public UserBeansGraphService(UserBeansDependencyService userDependenciesService) {
        this.userDependenciesService = userDependenciesService;
        this.webDocumentReader = new WebDocumentReader();
    }

    // @formatter:on

    // @formatter:off
    String generateGraphWebDocument() {
        logger.info("Generating Web Document");
        String fileName = "static/graph.html";
        return webDocumentReader.readFromResources(fileName);
    }

    // @formatter:on

    public record BeanNode(String beanName, String beanPackage, String dependency) {}

    public record Edge(BeanNode source, BeanNode target) {}

    public record GraphData(List<BeanNode> nodes, List<Edge> edges) {}

    List<BeanNode> getNodes() {
        var dependencyDocuments = userDependenciesService.getDependencyDocuments();
        List<BeanNode> beanNodeList = dependencyDocuments
            .stream()
            .map(dd -> new BeanNode(dd.beanName(), dd.beanPackage(), dd.dependency()))
            .distinct()
            .toList();
        List<String> listDependencies = dependencyDocuments.stream().flatMap(dd -> dd.beanDependencies().stream()).toList();
        List<BeanNode> matchingNodes = listDependencies
            .stream()
            .map(dep ->
                beanNodeList
                    .stream()
                    .filter(node -> dep.equals(node.beanName()))
                    .findFirst()
                    .orElse(new BeanNode(dep, UNKNOWN_PACKAGE, UNKNOWN_DEPENDENCY))
            )
            .toList();

        Set<BeanNode> distinctBeans = new HashSet<>(beanNodeList);
        distinctBeans.addAll(matchingNodes);

        return distinctBeans.stream().sorted(Comparator.comparing(BeanNode::beanName)).toList();
    }

    List<Edge> getEdges() {
        return userDependenciesService
            .getDependencyDocuments()
            .stream()
            .flatMap(dd -> {
                BeanNode sourceNode = new BeanNode(dd.beanName(), dd.beanPackage(), dd.dependency());
                if (!dd.beanDependencies().isEmpty()) {
                    // @formatter:off
                        return dd.beanDependencies().stream()
                                .map(dep -> new Edge(sourceNode, new BeanNode(dep, UNKNOWN_PACKAGE, UNKNOWN_DEPENDENCY)));
                    // @formatter:on
                } else {
                    //TODO False edge; an evidence to redesign the graph to be consumed for D3.js
                    return Stream.of(new Edge(sourceNode, null));
                }
            })
            .toList();
    }

    // @formatter:off
    GraphData generateGraphData(String dependencyFilter) {
        logger.info("Generating Graph data");

        var edges = getEdges();

        //TODO Remove in the future the filter. Everything will be filtered in D3.js side.
        if (Objects.isNull(dependencyFilter) || dependencyFilter.equals("ALL")) {
            return new GraphData(getNodes(), edges);
        } else {
            return new GraphData(getNodes(), edges.stream()
                    .filter(edge -> edge.source().dependency.contains(dependencyFilter))
                    .toList());
        }
    }

    // @formatter:on

    public record Dependency(String dependency) {}

    List<Dependency> generateGraphCombo() {
        return userDependenciesService
            .getDependencyDocuments()
            .stream()
            .map(UserBeansDependencyService.DependencyDocument::dependency)
            .filter(dependency -> !dependency.equals(UNKNOWN_DEPENDENCY))
            .map(Dependency::new)
            .distinct()
            .sorted(Comparator.comparing(Dependency::dependency))
            .toList();
    }
}
