package info.jab.userbeans;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

@RestController()
public class UserDependenciesController {

	@GetMapping(path= "/api/v1/user-beans/dependencies", produces = MediaType.APPLICATION_JSON_VALUE)
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

}
