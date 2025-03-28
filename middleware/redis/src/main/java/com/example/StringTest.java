package com.example;

import org.junit.jupiter.api.Assertions;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.redis.connection.DataType;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.concurrent.TimeUnit;

public class StringTest implements CommandLineRunner {

    private final ValueOperations<String, String> ops;
    private final StringRedisTemplate redisTemplate;

    StringTest(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
        ops = redisTemplate.opsForValue();
    }

    @Override
    public void run(String... args) throws Exception {
        string();
        increment();
    }

    private void string() {
        String key = "testValueString";
        // 可以同时设置时间
        ops.set(key, "test", 10, TimeUnit.SECONDS);
        String test = ops.get(key);
        Assertions.assertEquals("test", test);
        Assertions.assertEquals(DataType.STRING, redisTemplate.type(key));
    }

    private void increment() {
        String key = "testValueNumber";
        try {
            Assertions.assertEquals(1, ops.increment(key));
            Assertions.assertEquals(2, ops.increment(key));
            Assertions.assertEquals(4, ops.increment(key, 2));
            Assertions.assertEquals(1, ops.decrement(key, 3));
            Assertions.assertEquals(DataType.STRING, redisTemplate.type(key));
        } finally {
            redisTemplate.delete(key);
        }
    }

}
