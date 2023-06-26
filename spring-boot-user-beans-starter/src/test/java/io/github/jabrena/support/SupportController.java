package io.github.jabrena.support;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class SupportController {

    public record HelloWorld(String message) {}

    @GetMapping("demo")
    ResponseEntity<HelloWorld> getMessage() {
        return ResponseEntity.ok().body(new HelloWorld("Hello World"));
    }
}
