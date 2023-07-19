package io.github.jabrena.userbeans;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.jabrena.support.TestApplication;
import java.util.Comparator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

@SpringBootTest(classes = { TestApplication.class }, properties = { "management.endpoints.web.exposure.include=beans,userbeans" })
class UserBeansServiceTests {

    @Autowired
    private UserBeansService userBeansService;

    @Autowired
    private ApplicationContext applicationContext;

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
        var beanList = userBeansService.getBeansDocuments().stream().filter(bd -> bd.beanName().equals("")).toList();

        //Then
        assertThat(beanList).hasSize(expectedUnnamedBeanCounter);
    }

    @Test
    void shouldRetrieveAllBeans() {
        //Given
        var expectedUnnamedBeanCounter = 0;

        record BeanDocument(String beanName, String beanPackage) {}

        //When
        var beanList = userBeansService
            .getBeansDocuments()
            .stream()
            .filter(bd -> bd.dependencies().size() > 0)
            .flatMap(bd -> bd.dependencies().stream())
            .distinct()
            .map(str -> {
                try {
                    Class<?> myClass = applicationContext.getBean(str).getClass();
                    String beanName = (myClass.getSimpleName().length() == 0) ? myClass.getName() : myClass.getSimpleName();
                    String beanPackage = myClass.getPackageName();
                    return new BeanDocument(beanName, beanPackage);
                } catch (NoSuchBeanDefinitionException e) {
                    //Empty on purpose
                }
                return new BeanDocument(str, "UNKNOWN");
            })
            .sorted(Comparator.comparing(BeanDocument::beanName))
            .peek(System.out::println)
            .toList();

        //Then
        assertThat(beanList).hasSizeGreaterThan(expectedUnnamedBeanCounter);
    }
}
