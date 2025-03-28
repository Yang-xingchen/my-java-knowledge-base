package com.example;

import org.junit.jupiter.api.Assertions;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.redis.connection.DataType;
import org.springframework.data.redis.core.GeoOperations;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.Map;
import java.util.Set;

public class HashTest implements CommandLineRunner {

    private final HashOperations<String, Object, Object> ops;
    private final StringRedisTemplate redisTemplate;

    HashTest(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
        ops = redisTemplate.opsForHash();
    }

    @Override
    public void run(String... args) throws Exception {
        String key = "testHash";
        try {
            // {"k1":"v1"}
            ops.put(key, "k1", "v1");
            // {"k1":"v1","k2":"v2","k3":"v3"}
            ops.putAll(key, Map.of("k2", "v2", "k3", "v3"));
            // {"k1":"v1","k2":"v2","k3":"v3"}
            ops.put(key, "k1", "v1");

            Assertions.assertEquals(3, ops.size(key));
            Assertions.assertEquals(Set.of("k1", "k2", "k3"), ops.keys(key));

            Assertions.assertEquals(Map.of("k1","v1","k2", "v2", "k3", "v3"), ops.entries(key));

            // {"k2":"v2","k3":"v3"}
            Assertions.assertEquals(1, ops.delete(key, "k1"));
            Assertions.assertEquals(0, ops.delete(key, "k1"));
            Assertions.assertEquals(Set.of("k2", "k3"), ops.keys(key));

            Assertions.assertEquals(DataType.HASH, redisTemplate.type(key));
        } finally {
            redisTemplate.delete(key);
        }
    }

}
