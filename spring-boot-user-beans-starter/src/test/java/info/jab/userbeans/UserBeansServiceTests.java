package info.jab.userbeans;

import static org.assertj.core.api.Assertions.assertThat;

import info.jab.support.SupportController;
import info.jab.support.TestApplication;
import java.util.Comparator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(
    classes = { TestApplication.class, SupportController.class },
    properties = { "management.endpoints.web.exposure.include=beans,userbeans" }
)
public class UserBeansServiceTests {

    @Autowired
    private UserBeansService userBeansService;

    // @formatter:off
    @Test
    void shouldReturnsAllBeansInformation() {
        //Given
        //When
        var beanList = userBeansService.getBeansDocuments();

        //Then
        assertThat(beanList).isSortedAccordingTo(
                Comparator.comparing(UserBeansService.BeanDocument::beanName));
        assertThat(beanList).hasSizeGreaterThan(0);
    }

    // @formatter:on

    //TODO A possible small issue to raise in Micrometer
    @Test
    void shouldOnlyExistThreeBeansWithoutName() {
        var beanList = userBeansService.getBeansDocuments();
        var unnamedBeans = beanList
            .stream()
            .filter(beanDocument -> beanDocument.beanName().equals(""))
            .peek(System.out::println)
            .toList();
        assertThat(unnamedBeans).hasSize(3);
    }

    @Test
    void shouldIncludeSpecificSupportTest() {
        //Given
        //When
        var beanListFiltered = userBeansService
            .getBeansDocuments()
            .stream()
            .filter(beanDocument -> beanDocument.beanPackage().contains("info.jab.support"))
            .peek(System.out::println)
            .toList();

        //Then
        assertThat(beanListFiltered).hasSize(2);
    }
}
