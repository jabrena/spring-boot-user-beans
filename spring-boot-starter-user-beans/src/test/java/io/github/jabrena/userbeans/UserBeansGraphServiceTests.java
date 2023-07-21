package io.github.jabrena.userbeans;

import static io.github.jabrena.userbeans.UserBeansGraphService.UNKNOWN_DEPENDENCY;
import static io.github.jabrena.userbeans.UserBeansGraphService.UNKNOWN_PACKAGE;
import static org.assertj.core.api.Assertions.assertThat;

import io.github.jabrena.support.SupportController;
import io.github.jabrena.support.TestApplication;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(
    classes = { TestApplication.class, SupportController.class },
    properties = { "management.endpoints.web.exposure.include=beans,userbeans" }
)
class UserBeansGraphServiceTests {

    @Autowired
    private UserBeansGraphService userBeansGraphService;

    @Test
    void shouldReturnValidGraphWebDocument() {
        //Given
        //When
        var html = userBeansGraphService.generateGraphWebDocument();
        Document doc = Jsoup.parse(html);

        //Then
        assertThat(doc).isNotNull();
    }

    @Test
    void shouldReturnGraphData() {
        //Given
        var noFilter = "ALL";

        //When
        var resuls = userBeansGraphService.generateGraphData(noFilter);

        //Then
        assertThat(resuls.edges()).hasSizeGreaterThan(0);
    }

    @Test
    void shouldReturnGraphDataForUnknownDependency() {
        //Given
        var filter = "UNKNOWN";

        //When
        var resuls = userBeansGraphService.generateGraphData(filter);

        //Then
        assertThat(resuls.edges()).hasSizeGreaterThan(0).hasSizeLessThan(20);
    }

    @Test
    void shouldReturnGraphDataForMicrometerCore() {
        //Given
        var filter = "micrometer-core-1.11.0.jar";

        //When
        var resuls = userBeansGraphService.generateGraphData(filter);

        //Then
        assertThat(resuls.edges()).hasSizeGreaterThan(0).hasSizeLessThan(30);
    }

    @Test
    void shouldReturnGraphDataForMicrometerObservation() {
        //Given
        var filter = "micrometer-observation-1.11.0.jar";

        //When
        var resuls = userBeansGraphService.generateGraphData(filter);

        //Then
        assertThat(resuls.edges()).hasSize(1);
    }

    @Test
    @DisplayName("Presence of false Edges")
    void shouldExistEdgesWithNoTarget() {
        //Given
        var noFilter = "ALL";

        //When
        var list = userBeansGraphService.generateGraphData(noFilter).edges().stream().filter(edge -> Objects.isNull(edge.target())).toList();

        //Then
        assertThat(list).hasSizeGreaterThan(0);
    }

    @ParameterizedTest
    @ValueSource(strings = { "io.github.jabrena.support", "io.github.jabrena.userbeans" })
    void shouldBePresentSpecificBeans(String beanPackage) {
        //Given
        //When
        var resuls = userBeansGraphService
            .generateGraphData("ALL")
            .edges()
            .stream()
            .filter(edge -> edge.source().beanPackage().contains(beanPackage))
            .toList();

        //Then
        assertThat(resuls).hasSizeGreaterThan(0);
    }

    @Test
    void shouldGenerateNodes() {
        //Given
        //When
        var resuls = userBeansGraphService.getNodes();

        //Then
        assertThat(resuls).hasSizeGreaterThan(0);
    }

    @Test
    void getDependencyDocuments() {
        //Given
        //When
        var result = userBeansGraphService.getDependencyDocuments();

        //Then

        assertThat(result).as("Result of dependency documents should not be empty").isNotEmpty();
    }

    @Test
    void getDependencyDocumentsWithFilter() {
        //Given
        var dependency = "UNKNOWN";

        // @formatter:off

        //When
        var result = userBeansGraphService.getDependencyDocuments().stream()
                .filter(dd -> dd.dependency().contains(dependency))
                .toList();

        //Then
        assertThat(result)
                .as("Result of dependency documents should not be empty")
                .hasSizeGreaterThan(0);
        // @formatter:on
    }

    @Test
    void getDependencyDocumentsWithFilter2() {
        //Given
        var dependency = "micrometer-observation-1.11.0.jar";

        // @formatter:off

        //When
        var result = userBeansGraphService.getDependencyDocuments().stream()
                .filter(dd -> dd.dependency().contains(dependency))
                .peek(System.out::println)
                .toList();

        //Then
        assertThat(result)
                .as("Result of dependency documents should not be empty")
                .hasSizeGreaterThan(0);
        // @formatter:on
    }

    @Test
    void getDependencyDocumentsWithFilter3() {
        //Given
        var dependency = "jackson-databind-2.15.0.jar";

        // @formatter:off

        //When
        var result = userBeansGraphService.getDependencyDocuments().stream()
                .filter(dd -> dd.dependency().contains(dependency))
                .peek(System.out::println)
                .toList();

        //Then
        assertThat(result)
                .as("Result of dependency documents should not be empty")
                .hasSizeGreaterThan(0);
        // @formatter:on
    }

    @Test
    void shouldProvideDifferentNodes() {
        //Given
        var expectedUnnamedBeanCounter = 0;

        //When
        AtomicInteger counter = new AtomicInteger(0);

        var dependencyDocuments = userBeansGraphService.getDependencyDocuments();
        List<UserBeansGraphService.BeanNode> beanNodeList = dependencyDocuments
            .stream()
            .map(dd -> new UserBeansGraphService.BeanNode(dd.beanName(), dd.beanPackage(), dd.dependency(), counter.incrementAndGet()))
            .distinct()
            .toList();
        List<String> listDependencies = dependencyDocuments.stream().flatMap(dd -> dd.beanDependencies().stream()).toList();
        List<UserBeansGraphService.BeanNode> matchingNodes = listDependencies
            .stream()
            .map(dep ->
                beanNodeList
                    .stream()
                    .filter(node -> dep.equals(node.beanName()))
                    .findFirst()
                    .orElse(new UserBeansGraphService.BeanNode(dep, UNKNOWN_PACKAGE, UNKNOWN_DEPENDENCY, counter.incrementAndGet()))
            )
            .toList();

        Set<UserBeansGraphService.BeanNode> distinctBeans = new HashSet<>(beanNodeList);
        distinctBeans.addAll(matchingNodes);

        distinctBeans.stream().map(UserBeansGraphService.BeanNode::beanName).distinct().sorted().forEach(System.out::println);

        //Then
        assertThat(distinctBeans).hasSizeGreaterThan(expectedUnnamedBeanCounter);
    }
}
