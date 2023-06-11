package info.jab.ms;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.DependencyDescriptor;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.util.StringUtils;

@Disabled
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

		Arrays.stream(allBeanNames)
				.map(beanName -> {

					/* 
					Object bean = beanFactory.getBean(beanName);
        			String[] dependencies = beanFactory.getDependenciesForBean(beanName);
        
        			System.out.println("Dependencies for bean: " + beanName);
					for (String dependency : dependencies) {
						System.out.println(dependency);
					}


					if (StringUtils.hasText(beanName)) {
						String[] dependentBeanNames = BeanFactoryUtils.dependentBeanNames(beanFactory, beanName);
						
						System.out.println("Dependencies for bean: " + beanName);
						for (String dependentBeanName : dependentBeanNames) {
							System.out.println(dependentBeanName);
						}
        			}
					
					DependencyDescriptor[] dependencies = beanFactory.getBeanDefinition(beanName)
									.getResolvableDependencies();
							
					System.out.println("Dependencies for bean: " + beanName);
					for (DependencyDescriptor dependency : dependencies) {
						System.out.println(dependency.getResolvableType());
					}
					*/

					return new StringBuilder()
							.append(counter.getAndIncrement())
							.append(SPACE)
							.append(beanName)
							.toString();
				})
				.forEach(System.out::println);
	}

}
