package info.jab.userbeans;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
public class ChatGTPConfig {

    @Bean(name = "ChatGTPMapper")
    public ObjectMapper getMapper() {
        return new ObjectMapper();
    }
}
