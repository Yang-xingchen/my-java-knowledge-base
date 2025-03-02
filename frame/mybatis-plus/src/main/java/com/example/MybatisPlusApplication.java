package com.example;

import com.example.service.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class MybatisPlusApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder(MybatisPlusApplication.class)
                .web(WebApplicationType.NONE)
                .run(args);
    }

    @Bean
    public CommandLineRunner base() {
        return new BaseMain();
    }

    @Bean
    public CommandLineRunner wrapper() {
        return new WrapperMain();
    }

    @Bean
    public CommandLineRunner wrapperLambda() {
        return new WrapperLambdaMain();
    }

    @Bean
    public CommandLineRunner lambdaWrapper() {
        return new LambdaWrapperMain();
    }

    @Bean
    public CommandLineRunner activeRecord() {
        return new ActiveRecordMain();
    }

}
