package info.jab.userbeans;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import info.jab.userbeans.UserBeansService.BeanDetail;

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

@Service
public class UserDependenciesService {

	@Autowired
	private UserBeansService userBeansService;

	public record Dependency(String dependency) {}

	List<Dependency> getDependencies() {

		String classpath = System.getProperty("java.class.path");
		String[] classpathEntries = classpath.split(File.pathSeparator);

		//target/test-classes
		//target/classes
		return Arrays.stream(classpathEntries)
				.filter(path -> path.contains(".jar"))
				.map(removePath)
				.map(Dependency::new)
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
			// TODO: handle exception
		}

        return packages;
    }

	public record DependencyBeanDetail(String dependencyName, String beanName) {}

	List<DependencyBeanDetail> getDependenciesAndBeans() {

		List<DependencyBeanDetail> list = new ArrayList();

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

}
