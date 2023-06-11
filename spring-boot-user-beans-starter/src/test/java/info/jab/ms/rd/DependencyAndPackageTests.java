package info.jab.ms.rd;

import info.jab.support.TestApplication;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

@SpringBootTest(classes = TestApplication.class)
class DependencyAndPackageTests {

	//@Autowired
	//private ConfigurableApplicationContext context;

    @Test
    void testJars() throws Exception {

        System.out.println("###");

        Set<String> jars = listJarDependencies();

        for (String jar : jars) {
            System.out.println("JAR: " + jar);
        }
    }

    private Set<String> listJarDependencies() throws IOException {
        Set<String> jars = new HashSet<>();

        //ClassLoader classLoader = context.getClassLoader();
        //ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver(classLoader);

        String classpath = System.getProperty("java.class.path");
        String[] classpathEntries = classpath.split(File.pathSeparator);

        for (String classpathEntry : classpathEntries) {
            System.out.println(classpathEntry);

            if(classpathEntry.contains(".jar")) {
                Set<String> result =listPackagesInJar(classpathEntry);
                result.stream().forEach(System.out::println);
            }

        }

        return jars;
    }

    private Set<String> listPackagesInJar(String jarPath) throws IOException {
        Set<String> packages = new HashSet<>();

        try (JarFile jarFile = new JarFile(new File(jarPath))) {
            Enumeration<JarEntry> entries = jarFile.entries();
            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                if (entry.isDirectory()) {
                    String packagePath = entry.getName().replace('/', '.');
                    if (!packagePath.isEmpty()) {
                        if(!packagePath.contains("META-INF")) {
                            packages.add(packagePath);
                        }
                    }
                }
            }
        }

        return packages;
    }
}
