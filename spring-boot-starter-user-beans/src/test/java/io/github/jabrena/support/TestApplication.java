package io.github.jabrena.support;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * A spring boot application used to manage
 * the Spring Context for testing purposes.
 */
@SpringBootApplication(proxyBeanMethods = false)
public class TestApplication {

    public static void main(String[] args) {
        SpringApplication.run(TestApplication.class, args);
    }
}
