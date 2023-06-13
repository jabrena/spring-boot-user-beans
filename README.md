# Spring boot User Beans

[![CI Builds](https://github.com/jabrena/spring-boot-user-beans/actions/workflows/build.yaml/badge.svg?branch=main)](https://github.com/jabrena/spring-boot-user-beans/actions/workflows/build.yaml)

A project to learn about the Beans that you maintain in memory when you run your projects.

![](./docs/user-beans2.png)

## Requirements

- [x] Visualize Beans running in the container
- [x] List of user dependencies (Jars)
- [x] List of user dependencies (Jars) & packages
- [x] List of user beans
- [x] List of dependencies (Jars) & Beans
- [ ] Review quality of results
- [ ] Learn to disable beans not used

## Convention over configuration

Convention over configuration (also known as coding by convention) is a software design paradigm used by software frameworks that attempts to decrease the number of decisions that a developer using the framework is required to make without necessarily losing flexibility and don't repeat yourself (DRY) principles.

https://en.wikipedia.org/wiki/Convention_over_configuration

## How to run in local

```bash
mvn clean verify
mvn spring-boot:run -pl examples/hello-world/ -am
curl http://localhost:8080/graph1
curl http://localhost:8080/graph2
curl -v http://localhost:8080/api/v1/user-beans/dependencies
curl -v http://localhost:8080/api/v1/user-beans/dependencies/packages
curl -v http://localhost:8080/api/v1/user-beans/dependencies/beans
curl -v http://localhost:8080/api/v1/user-beans/beans
curl http://localhost:8080
```

## Configuration

Enabling this spring boot property to enable this feature:

```
management.endpoints.web.exposure.include=beans
```

## Spring Boot CLI

```
sdk install springboot
spring init -d=web,devtools --build=maven --force ./
```

## References

- https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/beans/factory/package-summary.html
- https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/context/package-summary.html
- https://docs.spring.io/spring-boot/docs/current/maven-plugin/reference/htmlsingle/
- https://www.jetbrains.com/help/idea/spring-diagrams.html#spring-beans-diagram
- https://github.com/making/beansviz-spring-boot-actuator
- https://docs.spring.io/spring-boot/docs/current/reference/html/cli.html#cli.using-the-cli
- https://github.com/j3soon/directed-graph-visualization
- https://d3js.org/
- https://www.webjars.org/all