package io.github.jabrena.userbeans;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
public class ChatGPTConfig {

    @Bean(name = "ChatGPTMapper")
    public ObjectMapper getMapper() {
        return new ObjectMapper();
    }
}
