package com.example;

import org.junit.jupiter.api.Assertions;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;

@EnableCaching
@SpringBootApplication
public class CacheApplication {

    public static void main(String[] args) {
        SpringApplication.run(CacheApplication.class, args);
    }

    @Bean
    public MyCache cache() {
        return new MyCache("source", false);
    }

    @Bean
    public CommandLineRunner test(SourceService service) {
        return args -> {
            String key = "key";
            Integer i1 = service.get(key);
            Assertions.assertEquals(i1, service.get(key));
            Assertions.assertEquals(i1, service.get(key));

            System.out.println("=== update ===");
            Integer i2 = service.update(key);
            Assertions.assertNotEquals(i1, i2);
            Assertions.assertNotEquals(i1, service.get(key));
            Assertions.assertEquals(i2, service.get(key));

            System.out.println("=== remove ===");
            service.remove(key);
            Integer i3 = service.get(key);
            Assertions.assertNotEquals(i1, i3);
            Assertions.assertNotEquals(i2, i3);
        };
    }

}
