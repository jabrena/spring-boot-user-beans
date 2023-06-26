package io.github.jabrena.userbeans;

import io.github.jabrena.userbeans.UserBeansService.BeanDocument;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.UnaryOperator;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class UserBeansDependencyService {

    private static final Logger logger = LoggerFactory.getLogger(UserBeansDependencyService.class);

    public static final String UNKNOWN_DEPENDENCY = "UNKNOWN";

    private final UserBeansService userBeansService;

    public UserBeansDependencyService(UserBeansService userBeansService) {
        this.userBeansService = userBeansService;
    }

    public record Dependency(String dependency) {}

    public record DependencyDetail(String dependencyName, List<String> packages) {}

    public record DependencyDocument(String beanName, String beanPackage, List<String> beanDependencies, String dependency) {}

    private UnaryOperator<String> removePath = fullPath -> {
        var pathParts = fullPath.split("\\/");
        return (pathParts.length > 0) ? pathParts[pathParts.length - 1] : fullPath;
    };

    //TODO change visibility

    // @formatter:off
    List<DependencyDetail> getDependenciesAndPackages() {
        List<DependencyDetail> list = new ArrayList<>();

        String classpath = System.getProperty("java.class.path");
        String[] classpathEntries = classpath.split(File.pathSeparator);

        for (String classpathEntry : classpathEntries) {
            if (classpathEntry.contains(".jar")) {
                var jar = removePath.apply(classpathEntry);
                List<String> pkgs = listPackagesInJar(classpathEntry).stream().toList();
                list.add(new DependencyDetail(jar, pkgs));
            }
        }

        return list.stream()
                .sorted(Comparator.comparing(DependencyDetail::dependencyName))
                .toList();
    }

    // @formatter:on

    private Set<String> listPackagesInJar(String jarPath) {
        Set<String> packages = new HashSet<>();

        try (JarFile jarFile = new JarFile(new File(jarPath))) {
            Enumeration<JarEntry> entries = jarFile.entries();
            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                if (entry.isDirectory()) {
                    String packagePath = entry.getName().replace('/', '.');
                    if (!packagePath.isEmpty() && !packagePath.contains("META-INF")) {
                        packagePath = packagePath.substring(0, packagePath.length() - 1);
                        packages.add(packagePath);
                    }
                }
            }
        } catch (IOException e) {
            logger.warn(e.getMessage());
        }
        return packages;
    }

    // @formatter:off
    public List<DependencyDocument> getDependencyDocuments() {
        List<BeanDocument> beanDocuments = userBeansService.getBeansDocuments();
        List<DependencyDetail> jars = getDependenciesAndPackages();

        return beanDocuments
            .stream()
            .map(bd -> {
                for (DependencyDetail dd : jars) {
                    if (dd.packages.contains(bd.beanPackage())) {
                        return new DependencyDocument(
                            bd.beanName(),
                            bd.beanPackage(),
                            bd.dependencies(),
                            dd.dependencyName()
                        );
                    }
                }
                return new DependencyDocument(
                        bd.beanName(),
                        bd.beanPackage(),
                        bd.dependencies(),
                        UNKNOWN_DEPENDENCY);
            })
            .sorted(Comparator.comparing(DependencyDocument::beanName))
            .toList();
    }

    // @formatter:on

    List<Dependency> getDependencies() {
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
