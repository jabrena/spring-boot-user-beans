package info.jab.ms;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

@SpringBootTest
class ListBeanDependencyTests {

	@Autowired
	private ApplicationContext applicationContext;

	@Autowired
	private AutowireCapableBeanFactory beanFactory;

	@Autowired
	private ConfigurableListableBeanFactory beanFactory2;

	@Test
	void contextLoads() {

		displayAllBeans();

	}

	private void displayAllBeans() {
		String[] allBeanNames = applicationContext.getBeanDefinitionNames();
		AtomicInteger counter = new AtomicInteger(1);
		String SPACE = " ";

		ConfigurableListableBeanFactory bf = 
			(ConfigurableListableBeanFactory) applicationContext.getAutowireCapableBeanFactory();
    
		Arrays.stream(allBeanNames)
				.map(beanName -> {

					String[] dependencies = bf.getDependenciesForBean(beanName);
        
        			//System.out.println("Dependencies for bean: " + beanName);
					for (String dependency : dependencies) {
						System.out.println(dependency);
					}

					return new StringBuilder()
							.append(counter.getAndIncrement())
							.append(SPACE)
							.append(beanName)
							.toString();
				})
				.forEach(System.out::println);
	}

}
