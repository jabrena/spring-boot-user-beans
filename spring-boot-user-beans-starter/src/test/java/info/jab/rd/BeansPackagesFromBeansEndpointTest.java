package info.jab.rd;

//import org.springframework.context.ApplicationContext;

import info.jab.support.TestApplication;
import java.util.Arrays;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.beans.BeansEndpoint;
import org.springframework.boot.actuate.beans.BeansEndpoint.BeanDescriptor;
import org.springframework.boot.actuate.beans.BeansEndpoint.ContextBeansDescriptor;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(
    classes = TestApplication.class,
    properties = { "management.endpoints.web.exposure.include=beans,userbeans" }
)
class BeansPackagesFromBeansEndpointTest {

    @Autowired
    private BeansEndpoint beansEndpoint;

    //@Autowired
    //private ApplicationContext applicationContext;

    //private BeanDefinitionRegistry beanDefinitionRegistry;

    @Test
    void getClassAndPackageOfBeans() {
        Map<String, ContextBeansDescriptor> context = beansEndpoint.beans().getContexts();
        context.forEach((key, value) -> {
            Map<String, BeanDescriptor> beans = value.getBeans();
            beans.forEach((key2, value2) -> {
                String beanName = key2;
                Object bean = value2;

                Class<?> beanClass = bean.getClass();
                String className = beanClass.getSimpleName();
                //String packageName = beanClass.getPackage().toString();

                try {
                    Class<?> clazz = Class.forName(className);
                    Package classPackage = clazz.getPackage();
                    String packageName = classPackage.getName();

                    System.out.println("Bean Name: " + beanName);
                    System.out.println("Class Name: " + className);
                    System.out.println("Package Name: " + packageName);
                    System.out.println("-----------------------------------");
                } catch (ClassNotFoundException e) {
                    // TODO Auto-generated catch block
                }
            });
        });
    }

    @Test
    void getDependencies() {
        //beanDefinitionRegistry =
        //(BeanDefinitionRegistry) applicationContext.getAutowireCapableBeanFactory();

        Map<String, ContextBeansDescriptor> context = beansEndpoint.beans().getContexts();
        context.forEach((key, value) -> {
            Map<String, BeanDescriptor> beans = value.getBeans();
            beans.forEach((key2, value2) -> {
                String beanName = key2;

                // Get the bean definition from the application context
                //BeanDefinition beanDefinition =
                //beanDefinitionRegistry.getBeanDefinition(beanName);

                // Get the dependency information
                String[] dependencies = value2.getDependencies();

                System.out.println("Bean Name: " + beanName);
                System.out.println("Dependencies: " + Arrays.toString(dependencies));
                System.out.println("-----------------------------------");
            });
        });
    }
}
