package info.jab.userbeans;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import info.jab.support.TestApplication;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(
    classes = TestApplication.class,
    properties = {"management.endpoints.web.exposure.include=beans,userbeans"})
public class UserBeansServiceTests {

    @Autowired
    private UserBeansService userBeansService;

    @Test
    void testGetBeansFromBeansEndpoint() {
        var result = userBeansService.getBeansFromBeansEndpoint();

        assertThat(result.size()).isEqualTo(214);
    }
}
