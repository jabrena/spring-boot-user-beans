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

@RestControllerEndpoint(id = "userbeans")
public class UserBeansEndpoint {

    private static final Logger logger = LoggerFactory.getLogger(UserBeansEndpoint.class);

    @Autowired
    private GraphService graphService;

    @Autowired
    private BeanExplanationService beanExplanationService;

    @GetMapping(path = "/", produces = MediaType.TEXT_HTML_VALUE)
    ResponseEntity<String> loadGraphWebDocument() {
        logger.info("GET /actuator/userbeans");
        return ResponseEntity.ok().body(graphService.generateGraphWebDocument());
    }

    // @formatter:off
    @GetMapping(path = "/graph", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<List<GraphService.Edge>> getGraph(
            @RequestParam(name = "dependency", required = false) String dependency) {
        logger.info("GET /actuator/userbeans/graph");
        return ResponseEntity.ok().body(graphService.generateGraphData(dependency));
    }

    // @formatter:on

    @GetMapping(path = "/graph-combo", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<List<UserDependenciesService.Dependency>> getGraphCombo() {
        logger.info("GET /actuator/userbeans/graph-combo");
        return ResponseEntity.ok().body(graphService.generateGraphCombo());
    }

    @GetMapping(path = "/details", produces = MediaType.TEXT_HTML_VALUE)
    ResponseEntity<String> loadDetailsWebDocument() {
        logger.info("GET /actuator/userbeans/details");
        return ResponseEntity.ok().body(beanExplanationService.generateDetailsWebDocument());
    }

    // @formatter:off
    @GetMapping(path = "/details-explanation", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<BeanExplanationService.DetailsExplanation> loadDetailsContentWebDocument(
        @RequestParam(name = "class") String bean,
        @RequestParam(name = "package") String packageName,
        @RequestParam(name = "dependency") String dependency) {
        logger.info("GET /actuator/userbeans/details-explanation");
        logger.info("Bean: {}", bean);
        logger.info("Package: {}", packageName);
        logger.info("Dependency: {}", dependency);
        return ResponseEntity.ok().body(beanExplanationService.generateDetailsContent(bean, packageName, dependency));
    }
    // @formatter:on
}
