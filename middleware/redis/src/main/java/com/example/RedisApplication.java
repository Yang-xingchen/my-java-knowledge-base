package com.example;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.StringRedisTemplate;

@SpringBootApplication
public class RedisApplication {

    public static void main(String[] args) {
        SpringApplication.run(RedisApplication.class, args);
    }

    @Bean
    public CommandLineRunner string(StringRedisTemplate redisTemplate) {
        return new StringTest(redisTemplate);
    }

    @Bean
    public CommandLineRunner list(StringRedisTemplate redisTemplate) {
        return new ListTest(redisTemplate);
    }

    @Bean
    public CommandLineRunner set(StringRedisTemplate redisTemplate) {
        return new SetTest(redisTemplate);
    }

    @Bean
    public CommandLineRunner hash(StringRedisTemplate redisTemplate) {
        return new HashTest(redisTemplate);
    }

    @Bean
    public CommandLineRunner zset(StringRedisTemplate redisTemplate) {
        return new ZsetTest(redisTemplate);
    }

    @Bean
    public CommandLineRunner stream(StringRedisTemplate redisTemplate) {
        return new StreamTest(redisTemplate);
    }

    @Bean
    public CommandLineRunner geo(StringRedisTemplate redisTemplate) {
        return new GeoTest(redisTemplate);
    }

    @Bean
    public CommandLineRunner hll(StringRedisTemplate redisTemplate) {
        return new HyperLogLogTest(redisTemplate);
    }

    @Bean
    public CommandLineRunner other(StringRedisTemplate redisTemplate) {
        return new OtherTest(redisTemplate);
    }

}
