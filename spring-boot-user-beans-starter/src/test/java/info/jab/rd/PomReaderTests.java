package info.jab.rd;

import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;

import java.io.FileReader;

import org.junit.jupiter.api.Test;

class PomReaderTests {

    @Test
    void readMaven() {
        try {
            MavenXpp3Reader reader = new MavenXpp3Reader();
            Model model = reader.read(new FileReader("pom.xml"));

            for (Dependency dependency : model.getDependencies()) {
                System.out.println(dependency.getGroupId() + ":" + dependency.getArtifactId() + ":" + dependency.getVersion());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
