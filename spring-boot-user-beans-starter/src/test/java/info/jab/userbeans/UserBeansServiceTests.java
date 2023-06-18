package info.jab.userbeans;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

import info.jab.support.TestApplication;
import info.jab.userbeans.UserBeansService.BeanDocument;
import info.jab.userbeans.UserDependenciesService.DependencyDocument;
import java.util.Comparator;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(
    classes = TestApplication.class,
    properties = { "management.endpoints.web.exposure.include=beans,userbeans" }
)
public class UserBeansServiceTests {

    @Autowired
    private UserBeansService userBeansService;

    @Test
    void testGetBeansFromBeansEndpoint() {
        var result = userBeansService.getBeansFromBeansEndpoint();

        assertThat(result).hasSizeGreaterThan(200);
    }

    @Test
    void testGetBeansFromApplicationContext() {
        var result = userBeansService.getBeansFromApplicationContext();

        assertThat(result).hasSizeGreaterThan(200);
    }

    @Test
    void testGetBeansDetails() {
        var result = userBeansService.getBeansDetails();

        result
            .stream()
            .forEach(bd -> {
                System.out.println(bd.beanName() + " | " + bd.beanPackage());
            });

        assertThat(result).hasSizeGreaterThan(100);
    }

    // @formatter:off
    @Test
    void shouldReturnsAllBeansInformation() {
        var result = userBeansService.getBeansDocuments();

        AtomicInteger counter = new AtomicInteger(0);
        result
            .stream()
            //.filter(bd -> !bd.beanPackage().contains("org.springframework.boot.actuate.beans"))
            .sorted(Comparator.comparing(BeanDocument::beanName))
            .forEach(bd -> {
                System.out.println(counter.incrementAndGet() + " "
                + bd.beanName() + " | "
                + bd.beanPackage());
            });

        assertThat(result).hasSizeGreaterThan(100);
    }

    // @formatter:on

    @Test
    void shouldBeTheSamePackage() {
        //Given
        var expectedExistBean = "Graph2Service";
        var list1 = userBeansService.getBeansFromApplicationContext();
        var list2 = userBeansService.getBeansFromBeansEndpoint();

        //assertTrue(list1.contains(expectedExistBean));
        //assertThat(list1.indexOf(expectedExistBean)).isNotEqualTo(-1);
        list1.stream().forEach(System.out::println);
    }
}
