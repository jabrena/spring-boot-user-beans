package io.github.jabrena.userbeans;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.jabrena.support.SupportController;
import io.github.jabrena.support.TestApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(
    classes = { TestApplication.class, SupportController.class },
    properties = { "management.endpoints.web.exposure.include=beans,userbeans" }
)
class UserBeansDependencyServiceTests {

    @Autowired
    private UserBeansDependencyService userDependenciesService;

    @Test
    void testGetDependencies() {
        //Given
        //When
        var list1 = userDependenciesService.getDependencies();

        //Then
        assertThat(list1).hasSizeGreaterThan(0);
    }

    @Test
    void testGetDependenciesAndPackages() {
        //Given
        //When
        var result = userDependenciesService.getDependencyPackages();

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
