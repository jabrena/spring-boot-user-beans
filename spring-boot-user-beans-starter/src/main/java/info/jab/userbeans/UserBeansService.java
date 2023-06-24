package info.jab.userbeans;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.actuate.beans.BeansEndpoint;
import org.springframework.boot.actuate.beans.BeansEndpoint.ContextBeansDescriptor;
import org.springframework.stereotype.Service;

@Service
public class UserBeansService {

    Logger logger = LoggerFactory.getLogger(UserBeansService.class);

    private final BeansEndpoint beansEndpoint;

    public UserBeansService(BeansEndpoint beansEndpoint) {
        this.beansEndpoint = beansEndpoint;
    }

    public record BeanDocument(String beanName, String beanPackage, List<String> dependencies) {}

    public List<BeanDocument> getBeansDocuments() {
        logger.info("Generating Beans information");
        Map<String, ContextBeansDescriptor> beansMap = beansEndpoint.beans().getContexts();
        var contextBeansDescriptorList = beansMap.values().stream().toList();
        return contextBeansDescriptorList
            .stream()
            .flatMap(cd -> cd.getBeans().entrySet().stream())
            .map(toBeanDocument)
            .sorted(Comparator.comparing(BeanDocument::beanName))
            .toList();
    }

    private UnaryOperator<String> removePackage = beanName -> {
        var beanNameParts = beanName.split("\\.");
        return (beanNameParts.length > 0) ? beanNameParts[beanNameParts.length - 1] : beanName;
    };

    // @formatter:off
    private Function<Map.Entry<String, BeansEndpoint.BeanDescriptor>, BeanDocument>
            toBeanDocument = bean -> {
        String beanName = bean.getValue().getType().getSimpleName();
        Class<?> beanClass = bean.getValue().getType();
        String packageName = beanClass.getPackageName();
        List<String> dependencies = Arrays
                .stream(bean.getValue().getDependencies())
                .map(removePackage)
                .toList();
        return new BeanDocument(beanName, packageName, dependencies);
    };

    // @formatter:on

    public record UserBean(String beanName, String packageName, Boolean isClass) {}

    public record BeanDocument2(UserBean parentBean, List<UserBean> dependencies) {}

    List<BeanDocument2> getBeansDocuments2() {
        logger.info("Generating Beans information");
        Map<String, ContextBeansDescriptor> beansMap = beansEndpoint.beans().getContexts();
        var contextBeansDescriptorList = beansMap.values().stream().toList();
        var result = contextBeansDescriptorList
            .stream()
            .flatMap(cd -> cd.getBeans().entrySet().stream())
            .map(toBeanDocument2)
            .toList();
        unknownClassCounter.set(0);
        return result;
    }

    AtomicInteger unknownClassCounter = new AtomicInteger(0);

    // @formatter:off
    private Function<Map.Entry<String, BeansEndpoint.BeanDescriptor>, BeanDocument2>
            toBeanDocument2 = bean -> {
        String beanName = bean.getValue().getType().getSimpleName();
        Class<?> beanClass = bean.getValue().getType();
        String packageName = beanClass.getPackageName();
        var dependencies =
                Arrays.stream(bean.getValue().getDependencies())
                        .map(dependency -> {
                            try {
                                Class<?> beanClassDependency = Class.forName(dependency);
                                String packageNameDependency = beanClassDependency.getPackageName();
                                Class<?> beanClassDependency2 =
                                        Class.forName(beanClassDependency.getSimpleName());
                                return new UserBean(
                                        beanClassDependency2.getSimpleName(),
                                        packageNameDependency, true);
                            } catch (ClassNotFoundException e) {
                                logger.warn("Dependency not found: {} {}",
                                        unknownClassCounter.incrementAndGet(),
                                        e.getMessage());
                                return new UserBean(
                                        e.getMessage(),
                                        "", false);
                            }
                        })
                        .toList();
        return new BeanDocument2(new UserBean(beanName, packageName, true), dependencies);
    };
    // @formatter:on
}
