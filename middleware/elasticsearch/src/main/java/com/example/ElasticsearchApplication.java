package com.example;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.data.elasticsearch.config.EnableElasticsearchAuditing;

@EnableElasticsearchAuditing
@SpringBootApplication
public class ElasticsearchApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder(ElasticsearchApplication.class)
                .web(WebApplicationType.NONE)
                .run(args);
    }

    @Bean
    public CommandLineRunner base() {
        return new BaseTest();
    }

}
