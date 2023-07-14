package io.github.jabrena.userbeans;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.List;
import java.util.function.UnaryOperator;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ClasspathDependencyService {

    private static final Logger logger = LoggerFactory.getLogger(ClasspathDependencyService.class);

    record ClasspathDependency(String dependency) {}

    public record DependencyPackage(String dependencyName, String packageName) {}

    List<ClasspathDependency> getClasspathDependencies() {
        String classpath = System.getProperty("java.class.path");
        String[] classpathEntries = classpath.split(File.pathSeparator);

        return Arrays
            .stream(classpathEntries)
            .filter(classpathEntry -> classpathEntry.contains(".jar"))
            .map(ClasspathDependency::new)
            .sorted(Comparator.comparing(ClasspathDependency::dependency))
            .toList();
    }

    private UnaryOperator<String> removePath = fullPath -> {
        var pathParts = fullPath.split("\\/");
        return (pathParts.length > 0) ? pathParts[pathParts.length - 1] : fullPath;
    };

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

    List<DependencyPackage> getDependencyPackages() {
        return getClasspathDependencies()
            .stream()
            .map(ClasspathDependency::dependency)
            .flatMap(classpathEntry -> {
                String jar = removePath.apply(classpathEntry);
                List<String> pkgs = listPackagesInJar(classpathEntry);
                return pkgs.stream().map(pkg -> new DependencyPackage(jar, pkg));
            })
            .toList();
    }
}
