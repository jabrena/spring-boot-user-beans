package io.github.jabrena.userbeans;

import io.github.jabrena.userbeans.UserBeansService.BeanDocument;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.List;
import java.util.Optional;
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
    public static final String UNKNOWN_PACKAGE = "UNKNOWN";

    private final UserBeansService userBeansService;

    public UserBeansDependencyService(UserBeansService userBeansService) {
        this.userBeansService = userBeansService;
    }

    public record Dependency(String dependency) {}

    public record DependencyPackage(String dependencyName, String packageName) {}

    public record DependencyDocument(String beanName, String beanPackage, List<String> beanDependencies, String dependency) {}

    private UnaryOperator<String> removePath = fullPath -> {
        var pathParts = fullPath.split("\\/");
        return (pathParts.length > 0) ? pathParts[pathParts.length - 1] : fullPath;
    };

    List<DependencyPackage> getDependencyPackages() {
        String classpath = System.getProperty("java.class.path");
        String[] classpathEntries = classpath.split(File.pathSeparator);

        return Arrays
            .stream(classpathEntries)
            .filter(classpathEntry -> classpathEntry.contains(".jar"))
            .flatMap(classpathEntry -> {
                String jar = removePath.apply(classpathEntry);
                logger.info(jar);
                List<String> pkgs = listPackagesInJar(classpathEntry);
                return pkgs.stream().map(pkg -> new DependencyPackage(jar, pkg));
            })
            .toList();
    }

    private List<String> listPackagesInJar(String jarPath) {
        List<String> packages = new ArrayList<>();

        try (JarFile jarFile = new JarFile(new File(jarPath))) {
            Enumeration<JarEntry> entries = jarFile.entries();
            List<JarEntry> entryList = Collections.list(entries);

            for (JarEntry entry : entryList) {
                if (entry.isDirectory()) {
                    String packagePath = entry.getName().replace('/', '.');
                    if (!packagePath.isEmpty() && !packagePath.startsWith("META-INF")) {
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
        List<DependencyPackage> jars = getDependencyPackages();

        return beanDocuments.stream()
                .map(bd -> {
                    Optional<DependencyPackage> matchingPackage = jars.stream()
                            .filter(pkg -> pkg.packageName().equals(bd.beanPackage()))
                            .findFirst();
                    String dependencyName = matchingPackage.map(DependencyPackage::dependencyName)
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
