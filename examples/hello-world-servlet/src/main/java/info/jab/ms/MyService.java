package info.jab.ms;

import org.springframework.stereotype.Service;

@Service
public class MyService {

    public record HelloWorld(String message) {}

    public HelloWorld getMessage() {
        return new HelloWorld("Hello World");
    }
}
