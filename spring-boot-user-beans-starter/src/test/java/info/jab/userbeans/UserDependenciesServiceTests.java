package info.jab.userbeans;

import static org.assertj.core.api.Assertions.assertThat;

import info.jab.support.TestApplication;
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
        assertThat(result).hasSizeGreaterThan(0);
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
    void testGetDependenciesAndPackages() {
        //Given
        //When
        var result = userDependenciesService.getDependenciesAndPackages();

        //Then
        assertThat(result).hasSizeGreaterThan(0);
    }

    @Test
    void getDependencyDocuments() {
        //Given
        //When
        var result = userDependenciesService.getDependencyDocuments();

        //Then
        assertThat(result).hasSizeGreaterThan(0);
    }
}
