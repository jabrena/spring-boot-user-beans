package info.jab.userbeans;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.actuate.beans.BeansEndpoint;
import org.springframework.boot.actuate.beans.BeansEndpoint.ContextBeansDescriptor;
import org.springframework.stereotype.Service;

@Service
public class UserBeansService {

    private static final Logger logger = LoggerFactory.getLogger(UserBeansService.class);

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
    private Function<Map.Entry<String, BeansEndpoint.BeanDescriptor>, BeanDocument> toBeanDocument = bean -> {
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
}
