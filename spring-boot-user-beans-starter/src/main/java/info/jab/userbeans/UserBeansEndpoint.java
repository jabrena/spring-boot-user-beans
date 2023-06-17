package info.jab.userbeans;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.endpoint.web.annotation.RestControllerEndpoint;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

import info.jab.userbeans.UserBeansService.BeanDetail;
import info.jab.userbeans.UserDependenciesService.DependencyBeanDetail;
import info.jab.userbeans.UserDependenciesService.DependencyDetail;

@RestControllerEndpoint(id = "userbeans")
public class UserBeansEndpoint {

	@Autowired
	private UserBeansService userBeansService;

	@Autowired
	private UserDependenciesService userDependenciesService;

	@GetMapping(path= "beans", produces = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<List<BeanDetail>> getBeans() {
		return ResponseEntity.ok(userBeansService.getBeansDetails());
	}

	@GetMapping(path= "dependencies", produces = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<List<String>> getDependencies() {
		return ResponseEntity.ok(userDependenciesService.getDependencies());
	}

	@GetMapping(path= "dependencies/packages", produces = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<List<DependencyDetail>> getDependenciesPackages() {
		return ResponseEntity.ok(userDependenciesService.getDependenciesAndPackages());
	}

	@GetMapping(path= "dependencies/beans", produces = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<List<DependencyBeanDetail>> getDependenciesBeans() {
		return ResponseEntity.ok(userDependenciesService.getDependenciesAndBeans());
	}

	//UX

	@Autowired
	private Graph2Service graph2Service;

	@GetMapping(path= "/", produces = MediaType.TEXT_HTML_VALUE)
	ResponseEntity<String> ux() {

		var html = "";
		try {
			html = Files.readString(Paths.get(getClass().getClassLoader().getResource("static/graph2.html").toURI()));
		} catch (IOException | URISyntaxException e) {
			e.printStackTrace();
		}

		return ResponseEntity.ok().body(html);
	}

	@GetMapping(path= "/graph2", produces = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<String> graph2() {
		return graph2Service.generateGraph2();
	}
}