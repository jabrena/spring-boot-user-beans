package info.jab.ms;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

@SpringBootTest
class ListBeanTests {

	@Autowired
	private ApplicationContext applicationContext;

	@Test
	void contextLoads() {

		displayAllBeans();

	}

	public void displayAllBeans() {
		String[] allBeanNames = applicationContext.getBeanDefinitionNames();
		AtomicInteger counter = new AtomicInteger(1);
		String SPACE = " ";

		Arrays.stream(allBeanNames)
				.map(beanName -> {
					return new StringBuilder()
							.append(counter.getAndIncrement())
							.append(SPACE)
							.append(beanName)
							.toString();
				})
				.forEach(System.out::println);
	}

}
