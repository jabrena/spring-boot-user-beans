package info.jab.userbeans;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

@RestController
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class UserDependenciesController {

	@GetMapping(path= "/api/v1/user-beans/dependencies")
	ResponseEntity<List<String>> getDependencies() {
		return ResponseEntity.ok(extractDependencies());
	}

	private List<String> extractDependencies() {

		String classpath = System.getProperty("java.class.path");
		String[] classpathEntries = classpath.split(File.pathSeparator);

		//target/test-classes
		//target/classes
		return Arrays.stream(classpathEntries)
				.filter(path -> path.contains(".jar"))
				.sorted()
				.map(removePath)
				.toList();
	}

	Function<String, String> removePath = (fullPath) -> {
		var pathParts = fullPath.split("\\/");
		return (pathParts.length > 0) ? pathParts[pathParts.length - 1] : fullPath;
	};

	@GetMapping(path= "/api/v1/user-beans/dependencies/packages")
	ResponseEntity<List<DependencyDetail>> getDependenciesPackages() {
		;
		return ResponseEntity.ok(listDependencyAndPackagesIncluded());
	}

	public record DependencyDetail(String dependencyName, String packageName) {}

	private List<DependencyDetail> listDependencyAndPackagesIncluded() {

		List<DependencyDetail> list = new ArrayList<>();

        String classpath = System.getProperty("java.class.path");
        String[] classpathEntries = classpath.split(File.pathSeparator);

        for (String classpathEntry : classpathEntries) {
            System.out.println(classpathEntry);

            if(classpathEntry.contains(".jar")) {
				var jar = classpathEntry;
                Set<String> result = listPackagesInJar(classpathEntry);
                var jarList = result.stream().map(pkg -> {
					return new DependencyDetail(jar, pkg);
				}).toList();
				list.addAll(jarList);
            }
        }

		return list;
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

}
