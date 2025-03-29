package com.example;

import org.junit.jupiter.api.Assertions;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.redis.connection.DataType;
import org.springframework.data.redis.core.HyperLogLogOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HyperLogLogTest implements CommandLineRunner {

    private final HyperLogLogOperations<String, String> ops;
    private final StringRedisTemplate redisTemplate;
    private final ValueOperations<String, String> valueOps;

    HyperLogLogTest(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
        ops = redisTemplate.opsForHyperLogLog();
        valueOps = redisTemplate.opsForValue();
    }

    @Override
    public void run(String... args) throws Exception {
        String key = "testHLL";
        try {
            // 存储50万条 [0, 100000)的数字
            Random random = new Random();
            int bound = 100000;
            try (ExecutorService service = Executors.newVirtualThreadPerTaskExecutor()) {
                for (int i = 0; i < 100_000; i++) {
                    service.submit(() -> {
                        ops.add(key,
                                Integer.toHexString(random.nextInt(bound)),
                                Integer.toHexString(random.nextInt(bound)),
                                Integer.toHexString(random.nextInt(bound)),
                                Integer.toHexString(random.nextInt(bound)),
                                Integer.toHexString(random.nextInt(bound))
                        );
                    });
                }
            }

            long size = ops.size(key);
            Assertions.assertTrue(size <= bound * 1.0081 && size >= bound * 0.0081);

            Long valueSize = valueOps.size(key);
            // 12304
            System.out.println(valueSize);

            Assertions.assertEquals(DataType.STRING, redisTemplate.type(key));
        } finally {
            redisTemplate.delete(key);
        }
    }

}
