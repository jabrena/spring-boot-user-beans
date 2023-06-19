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
        assertThat(beanList).hasSizeGreaterThan(200);
    }
}
