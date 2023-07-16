package io.github.jabrena.userbeans;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.jabrena.support.SupportController;
import io.github.jabrena.support.TestApplication;
import java.util.Comparator;
import org.junit.jupiter.api.Disabled;
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

    // @formatter:off

    //TODO A possible small issue to raise in Micrometer
    @Test
    void shouldOnlyExistThreeBeansWithoutName() {
        //Given
        //When
        var beanList = userBeansService.getBeansDocuments();
        var unnamedBeans = beanList.stream()
                .filter(beanDocument -> beanDocument.beanName().equals(""))
                .toList();

        //Then
        assertThat(unnamedBeans).hasSize(3);
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

    @Disabled
    @Test
    void shouldRetrieveBeanDependencyInfo() {
        //Given
        //When
        var list = userBeansService
            .getBeansDocuments()
            .stream()
            .filter(bd -> bd.dependencies().size() > 0)
            .flatMap(bd -> bd.dependencies().stream())
            .distinct()
            .sorted()
            .map(str -> {
                try {
                    Class<?> myClass = this.getClass().getClassLoader().loadClass(str);
                    System.out.println(myClass.getSimpleName());
                    return myClass.getSimpleName();
                } catch (ClassNotFoundException e) {
                    //Empty on purpose
                }
                return "NOT-FOUND-" + str;
            })
            //.peek(System.out::println)
            .toList();

        //Then
        assertThat(list).hasSizeGreaterThan(0);
    }
}
