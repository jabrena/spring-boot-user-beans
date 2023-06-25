# External tests

```
git clone https://github.com/spring-projects/spring-petclinic.git
```

In the spring-petclinic, add the dependency:

```xml
<dependency>
    <groupId>info.jab</groupId>
    <artifactId>spring-boot-user-beans-starter</artifactId>
    <version>0.1.0-SNAPSHOT</version>
</dependency>
```

And run from root pom.xml

```
./mvnw spring-boot:run -pl external-tests/spring-petclinic -am
```

```
              |\      _,,,--,,_
             /,`.-'`'   ._  \-;;,_
  _______ __|,4-  ) )_   .;.(__`'-'__     ___ __    _ ___ _______
 |       | '---''(_/._)-'(_\_)   |   |   |   |  |  | |   |       |
 |    _  |    ___|_     _|       |   |   |   |   |_| |   |       | __ _ _
 |   |_| |   |___  |   | |       |   |   |   |       |   |       | \ \ \ \
 |    ___|    ___| |   | |      _|   |___|   |  _    |   |      _|  \ \ \ \
 |   |   |   |___  |   | |     |_|       |   | | |   |   |     |_    ) ) ) )
 |___|   |_______| |___| |_______|_______|___|_|  |__|___|_______|  / / / /
 ==================================================================/_/_/_/

:: Built with Spring Boot :: 3.1.1

```

Open the webbrowser with the following url:

```
open http://localhost:8080/actuator/userbeans
```


## References

- https://github.com/spring-projects/spring-petclinic/tree/main
