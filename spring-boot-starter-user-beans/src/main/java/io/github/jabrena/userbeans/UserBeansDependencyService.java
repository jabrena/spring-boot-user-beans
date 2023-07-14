package io.github.jabrena.userbeans;

import io.github.jabrena.userbeans.UserBeansService.BeanDocument;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class UserBeansDependencyService {

    private static final Logger logger = LoggerFactory.getLogger(UserBeansDependencyService.class);

    public static final String UNKNOWN_DEPENDENCY = "UNKNOWN";
    public static final String UNKNOWN_PACKAGE = "UNKNOWN";

    private final UserBeansService userBeansService;
    private final ClasspathDependencyService classpathDependencyService;

    public UserBeansDependencyService(UserBeansService userBeansService, ClasspathDependencyService classpathDependencyService) {
        this.userBeansService = userBeansService;
        this.classpathDependencyService = classpathDependencyService;
    }

    public record Dependency(String dependency) {}

    public record DependencyDocument(String beanName, String beanPackage, List<String> beanDependencies, String dependency) {}

    // @formatter:off
    public List<DependencyDocument> getDependencyDocuments() {
        List<BeanDocument> beanDocuments = userBeansService.getBeansDocuments();
        List<ClasspathDependencyService.DependencyPackage> jars = classpathDependencyService.getDependencyPackages();

        return beanDocuments.stream()
                .map(bd -> {
                    Optional<ClasspathDependencyService.DependencyPackage> matchingPackage = jars.stream()
                            .filter(pkg -> pkg.packageName().equals(bd.beanPackage()))
                            .findFirst();
                    String dependencyName = matchingPackage.map(ClasspathDependencyService.DependencyPackage::dependencyName)
                            .orElse(UNKNOWN_DEPENDENCY);
                    return new DependencyDocument(
                            bd.beanName(),
                            bd.beanPackage(),
                            bd.dependencies(),
                            dependencyName
                    );
                })
                .sorted(Comparator.comparing(DependencyDocument::beanName))
                .toList();
    }

    // @formatter:on

    public List<Dependency> getUserBeanDependencies() {
        return getDependencyDocuments()
            .stream()
            .map(UserBeansDependencyService.DependencyDocument::dependency)
            .filter(dependency -> !dependency.equals(UNKNOWN_DEPENDENCY))
            .map(UserBeansDependencyService.Dependency::new)
            .distinct()
            .sorted(Comparator.comparing(UserBeansDependencyService.Dependency::dependency))
            .toList();
    }

    //TODO Remove in the future

    List<ClasspathDependencyService.DependencyPackage> getDependencyPackages() {
        return classpathDependencyService.getDependencyPackages();
    }
}
