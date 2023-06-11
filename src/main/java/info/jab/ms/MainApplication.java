package info.jab.ms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.jar.Attributes;
import java.util.jar.Manifest;


@SpringBootApplication
public class MainApplication {

	public static void main(String[] args) {
		SpringApplication.run(MainApplication.class, args);
		//readManifest();
	}

	private static void readManifest() {
		try {
			System.out.println("READING MANIFEST");

			URLClassLoader cl = (URLClassLoader) MainApplication.class.getClassLoader();
			URL url = cl.findResource("META-INF/MANIFEST.MF");
			Manifest manifest = new Manifest(url.openStream());
			Attributes attr = manifest.getMainAttributes();
			System.out.println(manifest.getMainAttributes().getValue("Manifest-Version"));

			/*
            InputStream inputStream = MainApplication.class.getResourceAsStream("/META-INF/MANIFEST.MF");
            Manifest manifest = new Manifest(inputStream);
            
            // Get the main attributes
            Attributes mainAttributes = manifest.getMainAttributes();
            
            // Iterate over all entries in the manifest
            for (Object entry : manifest.getEntries().keySet()) {
                Attributes attributes = manifest.getAttributes(entry.toString());
                if (attributes != null) {
                    System.out.println("Entry: " + entry);
                    for (Object key : attributes.keySet()) {
                        System.out.println(key + ": " + attributes.get(key));
                    }
                    System.out.println();
                }
            }
            
            // Close the input stream
            inputStream.close();
			 */
        } catch (IOException e) {
            e.printStackTrace();
        }
	}

}
