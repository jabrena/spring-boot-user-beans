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
    void getDependencyDocuments() {
        //Given
        //When
        var result = userDependenciesService.getDependencyDocuments();

        //Then

        assertThat(result).as("Result of dependency documents should not be empty").isNotEmpty();
    }

    @Test
    void getDependencyDocumentsWithFilter() {
        //Given
        var dependency = "UNKNOWN";

        // @formatter:off

        //When
        var result = userDependenciesService.getDependencyDocuments().stream()
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
        var result = userDependenciesService.getDependencyDocuments().stream()
                .filter(dd -> dd.dependency().contains(dependency))
                .toList();

        //Then
        assertThat(result)
                .as("Result of dependency documents should not be empty")
                .hasSizeGreaterThan(0);
        // @formatter:on
    }
}
