package info.jab.userbeans;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.endpoint.web.annotation.RestControllerEndpoint;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import info.jab.userbeans.Graph2Service.DependencyCombo;
import info.jab.userbeans.Graph2Service.EdgeOutput;
import info.jab.userbeans.UserBeansService.BeanDetail;
import info.jab.userbeans.UserDependenciesService.Dependency;
import info.jab.userbeans.UserDependenciesService.DependencyBeanDetail;
import info.jab.userbeans.UserDependenciesService.DependencyDetail;

@RestControllerEndpoint(id = "userbeans")
public class UserBeansEndpoint {

    Logger logger = LoggerFactory.getLogger(Graph2Service.class);

	@Autowired
	private UserBeansService userBeansService;

	@Autowired
	private UserDependenciesService userDependenciesService;

	@GetMapping(path= "beans", produces = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<List<BeanDetail>> getBeans() {
		return ResponseEntity.ok(userBeansService.getBeansDetails());
	}

	@GetMapping(path= "dependencies", produces = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<List<Dependency>> getDependencies() {
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
	ResponseEntity<String> loadWebDocument() {
		return ResponseEntity.ok().body(graph2Service.generateHTML());
	}

	@GetMapping(path= "/graph2", produces = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<List<EdgeOutput>> graph2(@RequestParam(required = false) String dependency) {
		return ResponseEntity.ok().body(graph2Service.generateGraph2(dependency));
	}

	@GetMapping(path= "/graph2-combo", produces = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<List<DependencyCombo>> graph_combo2() {
		return ResponseEntity.ok().body(graph2Service.generateGraph2Combo());
	}
}
