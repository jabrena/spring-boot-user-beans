package io.github.jabrena.userbeans;

import io.github.jabrena.userbeans.UserBeansService.BeanDocument;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class UserBeansDependencyService {

    public static final String UNKNOWN_DEPENDENCY = "UNKNOWN";
    public static final String UNKNOWN_PACKAGE = "UNKNOWN";

    private final UserBeansService userBeansService;
    private final ClasspathDependencyReader classpathDependencyReader;

    public UserBeansDependencyService(UserBeansService userBeansService) {
        this.userBeansService = userBeansService;
        this.classpathDependencyReader = new ClasspathDependencyReader();
    }

    public record Dependency(String dependency) {}

    public record DependencyDocument(String beanName, String beanPackage, List<String> beanDependencies, String dependency) {}

    // @formatter:off
    public List<DependencyDocument> getDependencyDocuments() {
        List<BeanDocument> beanDocuments = userBeansService.getBeansDocuments();
        List<ClasspathDependencyReader.DependencyPackage> jars = classpathDependencyReader.getDependencyPackages();

        return beanDocuments.stream()
                .map(bd -> {
                    Optional<ClasspathDependencyReader.DependencyPackage> matchingPackage = jars.stream()
                            .filter(pkg -> pkg.packageName().equals(bd.beanPackage()))
                            .findFirst();
                    String dependencyName = matchingPackage.map(ClasspathDependencyReader.DependencyPackage::dependencyName)
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
}
