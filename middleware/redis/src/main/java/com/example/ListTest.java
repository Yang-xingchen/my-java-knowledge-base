package com.example;

import org.junit.jupiter.api.Assertions;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.redis.connection.DataType;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class ListTest implements CommandLineRunner {

    private final ListOperations<String, String> ops;
    private final StringRedisTemplate redisTemplate;

    ListTest(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
        ops = redisTemplate.opsForList();
    }

    @Override
    public void run(String... args) throws Exception {
        String key = "testList";
        try {
            // [1]
            ops.rightPush(key, "1");
            // [1,2,3]
            ops.rightPushAll(key, "2", "3");
            // [0,1,2,3]
            ops.leftPush(key, "0");
            List<String> range = ops.range(key, 0, -1);
            Assertions.assertEquals(List.of("0", "1", "2", "3"), range);

            // [1,2,3]
            Assertions.assertEquals("0", ops.leftPop(key));
            // [1,2]
            Assertions.assertEquals("3", ops.rightPop(key));

            Assertions.assertEquals(2, ops.size(key));

            Assertions.assertEquals(DataType.LIST, redisTemplate.type(key));
        } finally {
            redisTemplate.delete(key);
        }
    }

}
