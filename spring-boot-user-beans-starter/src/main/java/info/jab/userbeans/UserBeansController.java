package info.jab.userbeans;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

@RestController
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class UserBeansController {

	@Autowired
	private ApplicationContext context;

	@GetMapping(path= "/api/v1/user-beans/beans")
	ResponseEntity<List<BeanDetail>> getDependencies() {
		return ResponseEntity.ok(getBeansDetails());
	}

	public record BeanDetail(String beanName, String beanPackage) {}

    List<BeanDetail> getBeansDetails() {

		List<BeanDetail> list = new ArrayList<>();

        String[] beanNames = context.getBeanDefinitionNames();

        for (String beanName : beanNames) {
            Object bean = context.getBean(beanName);
            Class<?> beanClass = bean.getClass();
			Package beanPackage = beanClass.getPackage();

			list.add(new BeanDetail(removePackage.apply(beanName), beanPackage.getName()));
        }

		return list;
    }

    Function<String, String> removePackage = (beanName) -> {
        var beanNameParts = beanName.split("\\.");
        return (beanNameParts.length > 0) ? beanNameParts[beanNameParts.length - 1] : beanName;
    };
}
