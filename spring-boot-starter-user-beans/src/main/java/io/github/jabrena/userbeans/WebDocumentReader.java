package io.github.jabrena.userbeans;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WebDocumentReader {

    private static final Logger logger = LoggerFactory.getLogger(WebDocumentReader.class);

    String readFromResources(String fileName) {
        String html = "";
        try (InputStream ioStream = this.getClass().getClassLoader().getResourceAsStream(fileName)) {
            if (Objects.isNull(ioStream)) {
                logger.error(fileName + " is not found");
                throw new IllegalArgumentException(fileName + " is not found");
            } else {
                StringBuilder stringBuilder = new StringBuilder();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(ioStream));

                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line);
                    stringBuilder.append(System.lineSeparator()); // Add line separator if needed
                }

                html = stringBuilder.toString();
            }
        } catch (IOException e) {
            logger.warn(e.getMessage(), e);
        }
        return html;
    }
}
