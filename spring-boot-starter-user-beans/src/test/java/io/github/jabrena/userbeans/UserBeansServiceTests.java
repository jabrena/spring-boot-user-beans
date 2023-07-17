package io.github.jabrena.userbeans;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.jabrena.support.SupportController;
import io.github.jabrena.support.TestApplication;
import java.util.Comparator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(
    classes = { TestApplication.class, SupportController.class },
    properties = { "management.endpoints.web.exposure.include=beans,userbeans" }
)
class UserBeansServiceTests {

    @Autowired
    private UserBeansService userBeansService;

    // @formatter:off
    @Test
    void shouldReturnsAllBeansInformation() {
        //Given
        //When
        var beanList = userBeansService.getBeansDocuments();

        //Then
        assertThat(beanList)
                .isSortedAccordingTo(Comparator.comparing(UserBeansService.BeanDocument::beanName))
                .hasSizeGreaterThan(0);
    }

    // @formatter:on

    @Test
    void shouldIncludeSpecificSupportTest() {
        //Given
        //When
        var beanListFiltered = userBeansService
            .getBeansDocuments()
            .stream()
            .filter(beanDocument -> beanDocument.beanPackage().contains("io.github.jabrena.support"))
            .toList();

        //Then
        assertThat(beanListFiltered).hasSize(2);
    }

    @Test
    void shouldNoReturnAnyUnnamedBean() {
        //Given
        var expectedUnnamedBeanCounter = 0;

        //When
        var beanList = userBeansService.getBeansDocuments().stream()
                .filter(bd -> bd.beanName().equals(""))
                .toList();

        //Then
        assertThat(beanList).hasSize(expectedUnnamedBeanCounter);
    }
}
