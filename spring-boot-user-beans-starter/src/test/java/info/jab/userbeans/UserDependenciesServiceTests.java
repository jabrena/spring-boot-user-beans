package info.jab.userbeans;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import info.jab.support.TestApplication;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(
    classes = TestApplication.class,
    properties = {"management.endpoints.web.exposure.include=beans,userbeans"})
public class UserDependenciesServiceTests {

    @Autowired
    private UserDependenciesService userDependenciesService;

    @Test
    void testGetDependencies() {

        //Given
        //When
        var result = userDependenciesService.getDependencies();

        //Then
        assertThat(result.size()).isGreaterThan(80);
    }

    @Test
    void testGetDependenciesAndBeans() {
        //Given
        //When
        var result = userDependenciesService.getDependenciesAndBeans();

        //Then
        assertThat(result.size()).isGreaterThan(100);
    }

    @Test
    void testGetDependenciesAndPackages() {
        //Given
        //When
        var result = userDependenciesService.getDependenciesAndPackages();

        //Then
        assertThat(result.size()).isGreaterThan(1500);
    }
}
