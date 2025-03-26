package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.HandlerMapping;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;

import java.util.Map;

@SpringBootApplication
public class WsWebfluxApplication {

    public static void main(String[] args) {
        SpringApplication.run(WsWebfluxApplication.class);
    }

    @Bean
    public HandlerMapping handlerMapping() {
        Map<String, ?> map = Map.of("/ws", new WsWebFluxHandle());
        return new SimpleUrlHandlerMapping(map, -1);
    }

}
