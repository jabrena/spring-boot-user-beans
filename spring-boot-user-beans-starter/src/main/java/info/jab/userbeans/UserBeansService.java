package info.jab.userbeans;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.beans.BeansEndpoint;
import org.springframework.boot.actuate.beans.BeansEndpoint.BeanDescriptor;
import org.springframework.boot.actuate.beans.BeansEndpoint.ContextBeansDescriptor;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Service
public class UserBeansService {

    Logger logger = LoggerFactory.getLogger(UserBeansService.class);

	@Autowired
	private BeansEndpoint beansEndpoint;

	@Autowired
	private ApplicationContext context;

	public record BeanDetail(String beanName, String beanPackage) {}

    List<String> getBeansFromBeansEndpoint() {
        List<String> beanList = new ArrayList<>();
        Map<String, ContextBeansDescriptor> context = beansEndpoint.beans().getContexts();
		context.forEach((key, value) -> {
			Map<String, BeanDescriptor> beans = value.getBeans();
			beans.forEach((key2, value2) -> {
				String beanName = value2.getType().getSimpleName();
                beanList.add(beanName);
			});
		});

        return beanList;
    }

    List<BeanDetail> getBeansDetails() {

        Function<String, String> removePackage = (beanName) -> {
            var beanNameParts = beanName.split("\\.");
            return (beanNameParts.length > 0) ? beanNameParts[beanNameParts.length - 1] : beanName;
        };

        var beanList = getBeansFromBeansEndpoint();

        List<BeanDetail> result = new ArrayList<>();
        for(String beanName : beanList) {
            try {
                Object bean = context.getBean(beanName);
                Class<?> beanClass = bean.getClass();
			    Package beanPackage = beanClass.getPackage();

			    result.add(new BeanDetail(removePackage.apply(beanName), beanPackage.getName()));
            } catch (NoSuchBeanDefinitionException e) {
                logger.error(e.getLocalizedMessage(), e);
            }
        }

        return result;
    }
}
