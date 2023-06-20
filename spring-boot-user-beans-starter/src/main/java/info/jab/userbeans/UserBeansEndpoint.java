package info.jab.userbeans;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.endpoint.web.annotation.RestControllerEndpoint;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

@RestControllerEndpoint(id = "userbeans")
public class UserBeansEndpoint {

    Logger logger = LoggerFactory.getLogger(UserBeansEndpoint.class);

    @Autowired
    private UserBeansService userBeansService;

    @Autowired
    private UserDependenciesService userDependenciesService;

    @GetMapping(path = "beans", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<List<UserBeansService.BeanDocument>> getBeans() {
        logger.info("GET /actuator/userbeans/beans");
        return ResponseEntity.ok(userBeansService.getBeansDocuments());
    }

    @GetMapping(path = "dependencies", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<List<UserDependenciesService.Dependency>> getDependencies() {
        logger.info("GET /actuator/userbeans/dependencies");
        return ResponseEntity.ok(userDependenciesService.getDependencies());
    }

    @GetMapping(path = "dependencies/beans", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<List<UserDependenciesService.DependencyBeanDetail>> getDependenciesBeans() {
        logger.info("GET /actuator/userbeans/dependencies/beans");
        return ResponseEntity.ok(userDependenciesService.getDependenciesAndBeans());
    }

    //UX

    @Autowired
    private GraphService graphService;

    @GetMapping(path = "/", produces = MediaType.TEXT_HTML_VALUE)
    ResponseEntity<String> loadWebDocument() {
        logger.info("GET /actuator/userbeans");
        return ResponseEntity.ok().body(graphService.generateWebDocument());
    }

    @GetMapping(path = "/graph", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<List<GraphService.Edge>> graph() {
        logger.info("GET /actuator/userbeans/graph");
        return ResponseEntity.ok().body(graphService.generateGraphData());
    }
}
