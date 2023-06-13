package info.jab.userbeans;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import info.jab.userbeans.UserDependenciesService.DependencyBeanDetail;
import info.jab.userbeans.UserDependenciesService.DependencyDetail;

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

	@Autowired
	private UserDependenciesService userDependenciesService;

	@GetMapping(path= "/api/v1/user-beans/dependencies")
	ResponseEntity<List<String>> getDependencies() {
		return ResponseEntity.ok(userDependenciesService.getDependencies());
	}

	@GetMapping(path= "/api/v1/user-beans/dependencies/packages")
	ResponseEntity<List<DependencyDetail>> getDependenciesPackages() {
		return ResponseEntity.ok(userDependenciesService.getDependenciesAndPackages());
	}

	@GetMapping(path= "/api/v1/user-beans/dependencies/beans")
	ResponseEntity<List<DependencyBeanDetail>> getDependenciesBeans() {
		return ResponseEntity.ok(userDependenciesService.getDependenciesAndBeans());
	}

}
