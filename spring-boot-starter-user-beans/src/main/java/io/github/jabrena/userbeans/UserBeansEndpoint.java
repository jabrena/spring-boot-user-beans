package io.github.jabrena.userbeans;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.endpoint.web.annotation.RestControllerEndpoint;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestControllerEndpoint(id = "user-beans")
public class UserBeansEndpoint {

    private static final Logger logger = LoggerFactory.getLogger(UserBeansEndpoint.class);

    @Autowired
    private UserBeansGraphService userBeansGraphService;

    @Autowired
    private UserBeansExplanationService beanExplanationService;

    @GetMapping(path = "/", produces = MediaType.TEXT_HTML_VALUE)
    ResponseEntity<String> loadGraphWebDocument() {
        logger.info("GET /actuator/userbeans");
        return ResponseEntity.ok().body(userBeansGraphService.generateGraphWebDocument());
    }

    // @formatter:off
    @GetMapping(path = "/graph", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<List<UserBeansGraphService.Edge>> getGraph(
            @RequestParam(name = "dependency", required = false) String dependency) {
        logger.info("GET /actuator/userbeans/graph");
        return ResponseEntity.ok().body(userBeansGraphService.generateGraphData(dependency));
    }

    @GetMapping(path = "/graph2", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<UserBeansGraphService.GraphData> getGraph2(
            @RequestParam(name = "dependency", required = false) String dependency) {
        logger.info("GET /actuator/userbeans/graph2");
        return ResponseEntity.ok().body(userBeansGraphService.generateGraphData2(dependency));
    }

    // @formatter:on

    @GetMapping(path = "/graph-combo", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<List<UserBeansDependencyService.Dependency>> getGraphCombo() {
        logger.info("GET /actuator/userbeans/graph-combo");
        return ResponseEntity.ok().body(userBeansGraphService.generateGraphCombo());
    }

    @GetMapping(path = "/details", produces = MediaType.TEXT_HTML_VALUE)
    ResponseEntity<String> loadDetailsWebDocument() {
        logger.info("GET /actuator/userbeans/details");
        return ResponseEntity.ok().body(beanExplanationService.generateDetailsWebDocument());
    }

    // @formatter:off
    @GetMapping(path = "/details-explanation", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<UserBeansExplanationService.DetailsExplanation> loadDetailsContentWebDocument(
        @RequestParam(name = "class") String bean,
        @RequestParam(name = "package") String packageName,
        @RequestParam(name = "dependency") String dependency) {
        logger.info("GET /actuator/userbeans/details-explanation");
        return ResponseEntity.ok().body(beanExplanationService.generateDetailsContent(bean, packageName, dependency));
    }
    // @formatter:on
}
