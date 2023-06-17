package info.jab.userbeans;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import info.jab.userbeans.UserBeansService.BeanDetail;
import info.jab.userbeans.UserBeansService.BeanDocument;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;

@Service
public class UserDependenciesService {

    private static final Logger logger = LoggerFactory.getLogger(UserDependenciesService.class);

	@Autowired
	private UserBeansService userBeansService;

	public record Dependency(String dependency, String classpath) {}

	List<Dependency> getDependencies() {

		String classpath = System.getProperty("java.class.path");
		String[] classpathEntries = classpath.split(File.pathSeparator);

		//target/test-classes
		//target/classes
		return Arrays.stream(classpathEntries)
				.filter(path -> path.contains(".jar"))
				//.map(removePath)
				.map(str -> new Dependency(removePath.apply(str), str))
				.sorted(Comparator.comparing(Dependency::dependency))
				.toList();
	}

	private Function<String, String> removePath = (fullPath) -> {
		var pathParts = fullPath.split("\\/");
		return (pathParts.length > 0) ? pathParts[pathParts.length - 1] : fullPath;
	};

	public record DependencyDetail(String dependencyName, String packageName) {}

	List<DependencyDetail> getDependenciesAndPackages() {

		List<DependencyDetail> list = new ArrayList<>();

        String classpath = System.getProperty("java.class.path");
        String[] classpathEntries = classpath.split(File.pathSeparator);

        for (String classpathEntry : classpathEntries) {
            if(classpathEntry.contains(".jar")) {
				var jar = removePath.apply(classpathEntry);
                Set<String> result = listPackagesInJar(classpathEntry);
                var jarList = result.stream().map(pkg -> {
					return new DependencyDetail(jar, pkg);
				}).toList();
				list.addAll(jarList);
            }
        }

		return list.stream()
			.sorted(Comparator.comparing(DependencyDetail::packageName))
			.toList();
    }

    private Set<String> listPackagesInJar(String jarPath) {
        Set<String> packages = new HashSet<>();

        try (JarFile jarFile = new JarFile(new File(jarPath))) {
            Enumeration<JarEntry> entries = jarFile.entries();
            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                if (entry.isDirectory()) {
                    String packagePath = entry.getName().replace('/', '.');
                    if (!packagePath.isEmpty()) {
                        if(!packagePath.contains("META-INF")) {
                            packages.add(packagePath);
                        }
                    }
                }
            }

		} catch (IOException e) {
			logger.warn(e.getMessage());
		}

        return packages;
    }

	public record DependencyBeanDetail(String dependencyName, String beanName) {}

	List<DependencyBeanDetail> getDependenciesAndBeans() {

		List<DependencyDetail> dependencyDetail = getDependenciesAndPackages();
		List<BeanDetail> beanList = userBeansService.getBeansDetails();

		return beanList.stream()
			.map(bean -> {
				var be = bean.beanName();
				var pkg = bean.beanPackage();
				var result = dependencyDetail.stream()
					.filter(dd -> {
						return dd.packageName().contains(pkg);
					})
					.findFirst();
				if(result.isPresent()) {
					return new DependencyBeanDetail(result.get().dependencyName(), be);
				} else {
					return new DependencyBeanDetail("UNKNOWN", be);
				}
			})
			.sorted(Comparator.comparing(DependencyBeanDetail::dependencyName))
			.toList();
	}

	public record DependencyDocument(
		String dependency, List<String> packages, String beanName,
		String beanPackage, List<String> beanDependencies) {}

	record Tuple(String beanName, String beanPackage) {};

	public List<DependencyDocument> getDependencyDocuments() {

		List<BeanDocument> beanDocuments = userBeansService.getBeansDocuments();
		List<Dependency> jars = getDependencies();
		return jars.stream()
			.flatMap(dep -> {
				List<String> jarPackages = listPackagesInJar(dep.classpath()).stream().toList();

				return beanDocuments.stream()
					.map(bd -> {
						if(jarPackages.contains(bd)) {
							return new DependencyDocument(
								dep.dependency(),
								jarPackages,
								bd.beanName(),
								bd.beanPackage(),
								bd.depedencies());
						}
						return new DependencyDocument(
							"UKNOWN",
							new ArrayList<>(),
							bd.beanName(),
							bd.beanPackage(),
							bd.depedencies());
					});

			})
			.toList();
	}

}
