package io.github.jabrena.userbeans;

import static io.github.jabrena.userbeans.UserBeansDependencyService.UNKNOWN_DEPENDENCY;
import static io.github.jabrena.userbeans.UserBeansDependencyService.UNKNOWN_PACKAGE;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
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

    // @formatter:off
    GraphData generateGraphData(String dependencyFilter) {
        logger.info("Generating Graph data");

        var edges = userDependenciesService.getDependencyDocuments().stream()
            .flatMap(dd -> {
                BeanNode sourceNode = new BeanNode(dd.beanName(), dd.beanPackage(), dd.dependency());
                return processDependencies(sourceNode, dd);
            })
            .toList();

        //TODO Remove in the future the filter. Everything will be filtered in D3.js side.
        if (Objects.isNull(dependencyFilter) || dependencyFilter.equals("ALL")) {
            return new GraphData(new ArrayList<>(), edges);
        } else {
            return new GraphData(new ArrayList<>(), edges.stream()
                    .filter(edge -> edge.source().dependency.contains(dependencyFilter))
                    .toList());
        }
    }

    // @formatter:on

    private Stream<Edge> processDependencies(BeanNode sourceNode, UserBeansDependencyService.DependencyDocument dd) {
        if (!dd.beanDependencies().isEmpty()) {
            // @formatter:off
            return dd.beanDependencies().stream()
                    .map(dep -> new Edge(sourceNode, new BeanNode(dep, UNKNOWN_PACKAGE, UNKNOWN_DEPENDENCY)));
            // @formatter:on
        } else {
            //TODO False edge; an evidence to redesign the graph to be consumed for D3.js
            return Stream.of(new Edge(sourceNode, null));
        }
    }

    List<UserBeansDependencyService.Dependency> generateGraphCombo() {
        return userDependenciesService.getUserBeanDependencies();
    }
}
