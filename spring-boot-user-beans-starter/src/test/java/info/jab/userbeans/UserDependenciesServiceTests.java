package info.jab.userbeans;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import info.jab.support.TestApplication;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(
    classes = TestApplication.class,
    properties = {"management.endpoints.web.exposure.include=beans,userbeans"})
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

    @Test
    void testGetDependenciesAndPackages() {
        //Given
        //When
        var result = userDependenciesService.getDependenciesAndPackages();

        //Then
        assertThat(result).hasSizeGreaterThan(1500);
    }
}
