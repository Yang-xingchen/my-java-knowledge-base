package com.example;

import org.junit.jupiter.api.Assertions;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.redis.connection.DataType;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;

import java.util.Map;
import java.util.Set;

public class ZsetTest implements CommandLineRunner {

    private final ZSetOperations<String, String> ops;
    private final StringRedisTemplate redisTemplate;

    ZsetTest(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
        ops = redisTemplate.opsForZSet();
    }

    @Override
    public void run(String... args) throws Exception {
        String key = "testZSet";
        try {
            // {1:"k1"}
            ops.add(key, "k1", 1);
            // {1:"k1",2:"k2",3:"k3"}
            ops.add(key, Set.of(ZSetOperations.TypedTuple.of("k2", 2.0), ZSetOperations.TypedTuple.of("k3", 3.0)));
            // {1.5:"k1",2:"k2",3:"k3"}
            ops.add(key, "k1", 1.5);

            Assertions.assertEquals(3, ops.size(key));
            // 按排名返回
            Assertions.assertEquals(Set.of("k1", "k2", "k3"), ops.range(key, 0, -1));
            Assertions.assertEquals(Set.of("k2", "k3"), ops.range(key, 1, -1));
            // 返回排名
            Assertions.assertEquals(0, ops.rank(key, "k1"));

            // 按分数返回
            Assertions.assertEquals(Set.of("k1", "k2", "k3"), ops.rangeByScore(key, 1, 3));
            Assertions.assertEquals(Set.of("k2", "k3"), ops.rangeByScore(key, 2, 3));
            // 返回分数
            Assertions.assertEquals(1.5, ops.score(key, "k1"));

            ops.incrementScore(key, "k1", 0.5);
            // 返回分数
            Assertions.assertEquals(2, ops.score(key, "k1"));

            // {2:"k2",3:"k3"}
            Assertions.assertEquals(1, ops.remove(key, "k1"));
            Assertions.assertEquals(0, ops.remove(key, "k1"));
            Assertions.assertEquals(Set.of("k2", "k3"), ops.range(key, 0, -1));

            Assertions.assertEquals(DataType.ZSET, redisTemplate.type(key));
        } finally {
            redisTemplate.delete(key);
        }
    }

}
