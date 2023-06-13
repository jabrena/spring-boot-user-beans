package info.jab.rd;

import java.util.concurrent.atomic.AtomicInteger;

import java.util.function.Function;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import info.jab.support.TestApplication;

@SpringBootTest(classes = TestApplication.class)
public class BeansAndDependencyTests {

	@Autowired
	private ApplicationContext context;

    @Test
    void test() {
        String[] beanNames = context.getBeanDefinitionNames();

        AtomicInteger counter = new AtomicInteger(0);
        for (String beanName : beanNames) {
            Object bean = context.getBean(beanName);
            Class<?> beanClass = bean.getClass();
            Package beanPackage = beanClass.getPackage();

            System.out.println(counter.incrementAndGet());
            System.out.println("Bean: " + removePackage.apply(beanName));
            System.out.println("Package: " + beanPackage.getName());
            System.out.println();
        }
    }

    Function<String, String> removePackage = (beanName) -> {
        var beanNameParts = beanName.split("\\.");
        return (beanNameParts.length > 0) ? beanNameParts[beanNameParts.length - 1] : beanName;
    };
}
