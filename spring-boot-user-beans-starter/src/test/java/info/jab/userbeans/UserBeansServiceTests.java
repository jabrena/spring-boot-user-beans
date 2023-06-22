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
public class UserBeansServiceTests {

    @Autowired
    private UserBeansService userBeansService;

    @Test
    void shouldReturnsAllBeansInformation() {
        var beanList = userBeansService.getBeansDocuments();

        beanList.stream().flatMap(b -> b.dependencies().stream()).forEach(System.out::println);
        assertThat(beanList).hasSizeGreaterThan(200);
    }

    @Test
    void shouldReturnsAllBeansInformation2() {
        var beanList = userBeansService.getBeansDocuments2();

        beanList.stream().flatMap(b -> b.dependencies().stream()).forEach(System.out::println);
        assertThat(beanList).hasSizeGreaterThan(200);
    }
}
