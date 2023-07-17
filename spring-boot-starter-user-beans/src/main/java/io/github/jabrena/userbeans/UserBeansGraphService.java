package io.github.jabrena.userbeans;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class UserBeansGraphService {

    private static final Logger logger = LoggerFactory.getLogger(UserBeansGraphService.class);

    public static final String UNKNOWN_DEPENDENCY = "UNKNOWN";
    public static final String UNKNOWN_PACKAGE = "UNKNOWN";
    private final UserBeansService userBeansService;
    private final ClasspathDependencyReader classpathDependencyReader;

    private final WebDocumentReader webDocumentReader;

    // @formatter:off
    public UserBeansGraphService(UserBeansService userBeansService) {
        this.userBeansService = userBeansService;
        this.classpathDependencyReader = new ClasspathDependencyReader();
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

    public record DependencyDocument(String beanName, String beanPackage, List<String> beanDependencies, String dependency) {}

    // @formatter:off
    public List<DependencyDocument> getDependencyDocuments() {
        List<UserBeansService.BeanDocument> beanDocuments = userBeansService.getBeansDocuments();
        List<ClasspathDependencyReader.DependencyPackage> jars = classpathDependencyReader.getDependencyPackages();

        return beanDocuments.stream()
                .map(bd -> {
                    Optional<ClasspathDependencyReader.DependencyPackage> matchingPackage = jars.stream()
                            .filter(pkg -> pkg.packageName().equals(bd.beanPackage()))
                            .findFirst();
                    String dependencyName = matchingPackage.map(ClasspathDependencyReader.DependencyPackage::dependencyName)
                            .orElse(UNKNOWN_DEPENDENCY);
                    return new DependencyDocument(
                            bd.beanName(),
                            bd.beanPackage(),
                            bd.dependencies(),
                            dependencyName
                    );
                })
                .sorted(Comparator.comparing(DependencyDocument::beanName))
                .toList();
    }

    // @formatter:on

    public record BeanNode(String beanName, String beanPackage, String dependency, Integer index) {}

    public record BeanEdge(String beanName, String beanPackage, String dependency) {}

    public record Edge(BeanEdge source, BeanEdge target) {}

    public record GraphData(List<BeanNode> nodes, List<Edge> edges) {}

    List<BeanNode> getNodes() {
        AtomicInteger counter = new AtomicInteger(0);

        var dependencyDocuments = this.getDependencyDocuments();
        List<BeanNode> beanNodeList = dependencyDocuments
            .stream()
            .map(dd -> new BeanNode(dd.beanName(), dd.beanPackage(), dd.dependency(), counter.incrementAndGet()))
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
                    .orElse(new BeanNode(dep, UNKNOWN_PACKAGE, UNKNOWN_DEPENDENCY, counter.incrementAndGet()))
            )
            .toList();

        Set<BeanNode> distinctBeans = new HashSet<>(beanNodeList);
        distinctBeans.addAll(matchingNodes);

        return distinctBeans.stream().sorted(Comparator.comparing(BeanNode::beanName)).toList();
    }

    List<Edge> getEdges() {
        return this.getDependencyDocuments()
            .stream()
            .flatMap(dd -> {
                BeanEdge sourceNode = new BeanEdge(dd.beanName(), dd.beanPackage(), dd.dependency());
                if (!dd.beanDependencies().isEmpty()) {
                    // @formatter:off
                        return dd.beanDependencies().stream()
                                .map(dep -> new Edge(sourceNode, new BeanEdge(dep, UNKNOWN_PACKAGE, UNKNOWN_DEPENDENCY)));
                    // @formatter:on
                } else {
                    //TODO False edge; an evidence to redesign the graph to be consumed for D3.js
                    return Stream.of(new Edge(sourceNode, null));
                }
            })
            .sorted(Comparator.comparing(edge -> edge.source().beanName()))
            .toList();
    }

    // @formatter:off
    GraphData generateGraphData(String dependencyFilter) {
        logger.info("Generating Graph data");

        var edges = getEdges();

        //TODO Remove in the future the filter. Everything will be filtered in D3.js side.
        if (Objects.isNull(dependencyFilter) || dependencyFilter.equals("ALL")) {
            return new GraphData(new ArrayList<>(), edges);
        } else {
            var filteredEdges = edges.stream()
                    .filter(edge -> edge.source().dependency.contains(dependencyFilter))
                    .toList();
            return new GraphData(new ArrayList<>(), filteredEdges);
        }
    }

    // @formatter:on

    public record Dependency(String dependency) {}

    List<Dependency> generateGraphCombo() {
        return this.getDependencyDocuments()
            .stream()
            .map(DependencyDocument::dependency)
            .filter(dependency -> !dependency.equals(UNKNOWN_DEPENDENCY))
            .map(Dependency::new)
            .distinct()
            .sorted(Comparator.comparing(Dependency::dependency))
            .toList();
    }
}
