package info.jab.userbeans;

import static org.assertj.core.api.Assertions.assertThat;

import ch.qos.logback.core.model.processor.DependencyDefinition;
import info.jab.support.TestApplication;
import info.jab.userbeans.UserDependenciesService.DependencyDocument;
import java.io.File;
import java.util.Comparator;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(
    classes = TestApplication.class,
    properties = { "management.endpoints.web.exposure.include=beans,userbeans" }
)
class UserDependenciesServiceTests {

    @Autowired
    private UserDependenciesService userDependenciesService;

    @Test
    void testGetDependencies() {
        //Given
        //When
        var result = userDependenciesService.getDependencies();

        //Then
        assertThat(result).hasSizeGreaterThan(80);
    }

    @Test
    void testGetDependenciesAndBeans() {
        //Given
        //When
        var results = userDependenciesService.getDependenciesAndBeans();

        //Then
        assertThat(results).hasSizeGreaterThan(100);
    }

    // @formatter:off
    @Test
    void testGetDependenciesAndBeansWithFilter() {
        //Given
        var dependencyToFilter = "UNKNOWN";

        //When
        var results = userDependenciesService.getDependenciesAndBeans();
        var resultsFilterd = results.stream()
            .filter(dbd -> dbd.dependencyName().equals(dependencyToFilter))
            .toList();

        //Then
        assertThat(resultsFilterd).hasSizeGreaterThan(0);
    }

    // @formatter:on

    @Test
    void testGetDependenciesAndPackages() {
        //Given
        //When
        var result = userDependenciesService.getDependenciesAndPackages();

        //Then
        assertThat(result).hasSizeGreaterThan(1500);
    }

    @Test
    void getDependencyDocuments() {
        //Given
        //When
        var result = userDependenciesService.getDependencyDocuments();

        //Then
        AtomicInteger counter = new AtomicInteger(0);
        result
            .stream()
            //.filter(dd -> dd.dependency().contains("spring-boot-actuator"))
            .sorted(Comparator.comparing(DependencyDocument::dependency))
            //.limit(2)
            .forEach(dd -> {
                counter.incrementAndGet();
                System.out.println(dd.dependency() + " " + dd.packages().size());
            });
        System.out.println(counter);
    }
}
