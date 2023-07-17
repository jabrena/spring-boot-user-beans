package io.github.jabrena.userbeans;


import io.github.jabrena.support.SupportController;
import io.github.jabrena.support.TestApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.beans.BeansEndpoint;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.UnaryOperator;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(
        classes = { TestApplication.class, SupportController.class },
        properties = { "management.endpoints.web.exposure.include=beans,userbeans" }
)
class UnnamedMicrometerBeanTests {

    @Autowired
    private BeansEndpoint beansEndpoint;

    private UnaryOperator<String> removePackage = beanName -> {
        var beanNameParts = beanName.split("\\.");
        return (beanNameParts.length > 0) ? beanNameParts[beanNameParts.length - 1] : beanName;
    };

    private Function<Map.Entry<String, BeansEndpoint.BeanDescriptor>, UserBeansService.BeanDocument> toBeanDocument = bean -> {
        String beanName = bean.getValue().getType().getSimpleName();
        Class<?> beanClass = bean.getValue().getType();
        String packageName = beanClass.getPackageName();
        List<String> dependencies = Arrays
                .stream(bean.getValue().getDependencies())
                .map(removePackage)
                .toList();

        return new UserBeansService.BeanDocument(beanName, packageName, dependencies);
    };

    @Test
    void shouldExist3UnnamedBeansFromMicrometerCore() {

        //Given
        int expectedUnnamedBeansCount = 3;

        //When
        Map<String, BeansEndpoint.ContextBeansDescriptor> beansMap = beansEndpoint.beans().getContexts();
        var contextBeansDescriptorList = beansMap.values().stream().toList();
        var unnamedBeanList = contextBeansDescriptorList
                .stream()
                .flatMap(cd -> cd.getBeans().entrySet().stream())
                .map(toBeanDocument)
                .filter(beanDocument -> beanDocument.beanName().equals(""))
                .sorted(Comparator.comparing(UserBeansService.BeanDocument::beanPackage))
                .peek(System.out::println)
                .toList();

        //Then
        assertThat(unnamedBeanList).hasSize(expectedUnnamedBeansCount);
    }

}
