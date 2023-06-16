package info.jab.userbeans;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

@Service
public class UserBeansService {

	@Autowired
	private ApplicationContext context;

	public record BeanDetail(String beanName, String beanPackage) {}

    List<BeanDetail> getBeansDetails() {

		List<BeanDetail> list = new ArrayList<>();

        String[] beanNames = context.getBeanDefinitionNames();

        Function<String, String> removePackage = (beanName) -> {
            var beanNameParts = beanName.split("\\.");
            return (beanNameParts.length > 0) ? beanNameParts[beanNameParts.length - 1] : beanName;
        };

        for (String beanName : beanNames) {
            Object bean = context.getBean(beanName);
            Class<?> beanClass = bean.getClass();
			Package beanPackage = beanClass.getPackage();

			list.add(new BeanDetail(removePackage.apply(beanName), beanPackage.getName()));
        }

		return list;
    }
}
