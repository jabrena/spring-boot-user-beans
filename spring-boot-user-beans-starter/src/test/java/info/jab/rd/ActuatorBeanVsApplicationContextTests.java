package info.jab.rd;

import static org.assertj.core.api.Assertions.assertThat;

import info.jab.support.TestApplication;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.beans.BeansEndpoint;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

@SpringBootTest(
    classes = TestApplication.class,
    properties = { "management.endpoints.web.exposure.include=beans,userbeans" }
)
class ActuatorBeanVsApplicationContextTests {

    @Autowired
    private BeansEndpoint beansEndpoint;

    @Autowired
    private ApplicationContext applicationContext;

    @Disabled("Research")
    @Test
    void shouldBeTheSameNumber() {
        var counter1 = getBeansFromActuator().size();
        var counter2 = getBeansFromApplicationContext().size();
        assertThat(counter1).isEqualTo(counter2);
    }

    // @formatter:off
    @Disabled("Research")
    @Test
    void shouldBeEmpty() {
        var list1 = getBeansFromActuator().stream().map(String::toLowerCase).toList();
        var list2 = getBeansFromApplicationContext().stream().map(String::toLowerCase).toList();
        List<String> differences;

        if (list1.size() > list2.size()) {
            differences = new ArrayList<>(list1);
            differences.removeAll(list2);
        } else {
            differences = new ArrayList<>(list2);
            differences.removeAll(list1);
        }
        final String space = " ";

        AtomicInteger counter3 = new AtomicInteger(1);

        differences
            .stream()
            .map(beanName -> {
                return new StringBuilder()
                    .append(counter3.getAndIncrement())
                    .append(space)
                    .append(beanName)
                    .toString();
            })
            .forEach(System.out::println);
        assertThat(differences.size()).isEqualTo(0);
    }

    private List<String> getBeansFromActuator() {
        List<String> actuatoBeanList = new ArrayList<>();
        Map<String, BeansEndpoint.ContextBeansDescriptor> context =
            beansEndpoint.beans().getContexts();

        context.forEach((key, value) -> {
            Map<String, BeansEndpoint.BeanDescriptor> beans = value.getBeans();
            beans.forEach((key2, value2) -> {
                actuatoBeanList.add(value2.getType().getSimpleName());
            });
        });

        return actuatoBeanList;
    }

    // @formatter:on

    private List<String> getBeansFromApplicationContext() {
        String[] allBeanNames = applicationContext.getBeanDefinitionNames();
        return Arrays.stream(allBeanNames).map(removePackage).toList();
    }

    Function<String, String> removePackage = beanName -> {
        var beanNameParts = beanName.split("\\.");
        return (beanNameParts.length > 0) ? beanNameParts[beanNameParts.length - 1] : beanName;
    };
}
