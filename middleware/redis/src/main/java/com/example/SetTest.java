package com.example;

import org.junit.jupiter.api.Assertions;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.redis.connection.DataType;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.Set;
import java.util.concurrent.TimeUnit;

public class SetTest implements CommandLineRunner {

    private final SetOperations<String, String> ops;
    private final StringRedisTemplate redisTemplate;

    SetTest(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
        ops = redisTemplate.opsForSet();
    }

    @Override
    public void run(String... args) throws Exception {
        String key = "testSet";
        try {
            // [0]
            ops.add(key, "0");
            // [0,1,2]
            ops.add(key, "1", "2");
            // [0,1,2]
            ops.add(key, "1", "2");
            Assertions.assertEquals(Set.of("0", "1", "2"), ops.members(key));

            // 随机取
            Assertions.assertTrue(Set.of("0", "1", "2").contains(ops.randomMember(key)));

            Assertions.assertEquals(3, ops.size(key));

            // [1,2]
            Assertions.assertEquals(1, ops.remove(key, "0"));
            Assertions.assertEquals(0, ops.remove(key, "0"));
            Assertions.assertEquals(Set.of("1", "2"), ops.members(key));

            Assertions.assertEquals(Boolean.TRUE, ops.isMember(key, "1"));
            Assertions.assertEquals(Boolean.FALSE, ops.isMember(key, "0"));

            Assertions.assertEquals(DataType.SET, redisTemplate.type(key));
        } finally {
            redisTemplate.delete(key);
        }
    }

}
