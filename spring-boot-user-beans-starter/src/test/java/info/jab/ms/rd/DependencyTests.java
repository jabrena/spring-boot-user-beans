package info.jab.ms.rd;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.util.Arrays;

import info.jab.support.TestApplication;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

@SpringBootTest(classes = TestApplication.class)
class DependencyTests {

    @Test
    void shouldBeAMinimumDependencyList() {

        String classpath = System.getProperty("java.class.path");
        String[] classpathEntries = classpath.split(File.pathSeparator);

        final String SPACE = " ";
        AtomicInteger index = new AtomicInteger(1);

        //target/test-classes
        //target/classes
        Arrays.stream(classpathEntries)
                .filter(path -> path.contains(".jar"))
                .sorted()
                .map(removePath)
                .map(beanName -> {
                    return new StringBuilder()
                            .append(index.getAndIncrement())
                            .append(SPACE)
                            .append(beanName)
                            .toString();
                })
                .forEach(System.out::println);
    }

    Function<String, String> removePath = (fullPath) -> {
        var pathParts = fullPath.split("\\/");
        return (pathParts.length > 0) ? pathParts[pathParts.length - 1] : fullPath;
    };
}
