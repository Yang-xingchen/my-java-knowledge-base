package com.example;

import org.junit.jupiter.api.Assertions;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class OtherTest implements CommandLineRunner {

    private final StringRedisTemplate redisTemplate;
    private final DefaultRedisScript<String> typeScript = new DefaultRedisScript<>("return redis.call('type', KEYS[1])", String.class);


    OtherTest(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void run(String... args) throws Exception {
        json();
        bf();
        cf();
        tdigest();
        topk();
        cms();
        ts();
    }

    private <T> T execute(RedisScript<T> script, String... arg) {
        return redisTemplate.execute(script, List.of(arg));
    }

    private String type(String key) {
        return execute(typeScript, key);
    }

    /**
     * JSON
     */
    private void json() {
        String key = "testJSON";
        DefaultRedisScript<String> setScript = new DefaultRedisScript<>("return redis.call('JSON.SET', KEYS[1], KEYS[2], KEYS[3])", String.class);
        DefaultRedisScript<String> getScript = new DefaultRedisScript<>("return redis.call('JSON.GET', KEYS[1], KEYS[2])", String.class);
        try {
            execute(setScript, key, "$", "{\"s1\": \"s1\", \"num\": 1, \"arr\": [\"a0\", 2, null, true], \"obj\": {\"obj1\": {\"s11\": \"s11\"}, \"n11\": 3}}");

            Assertions.assertEquals("[\"s1\"]", execute(getScript, key, "$.s1"));
            Assertions.assertEquals("[\"s1\"]", execute(getScript, key, "$['s1']"));
            Assertions.assertEquals("[1]", execute(getScript, key, "$.num"));
            Assertions.assertEquals("[\"a0\"]", execute(getScript, key, "$.arr[0]"));
            Assertions.assertEquals("[2]", execute(getScript, key, "$.arr[-3]"));
            Assertions.assertEquals("[null]", execute(getScript, key, "$.arr[2]"));
            Assertions.assertEquals("[true]", execute(getScript, key, "$.arr[3]"));
            Assertions.assertEquals("[\"s11\"]", execute(getScript, key, "$.obj.obj1.s11"));
            Assertions.assertEquals("[\"s11\"]", execute(getScript, key, "$['obj']['obj1']['s11']"));
            Assertions.assertEquals("[\"s11\"]", execute(getScript, key, "$['obj'].obj1['s11']"));
            Assertions.assertEquals("[3]", execute(getScript, key, "$.obj.n11"));

            Assertions.assertEquals("[\"s1\",1]", execute(getScript, key, "$['s1','num']"));

            Assertions.assertEquals("[\"a0\",2]", execute(getScript, key, "$.arr[:2]"));
            Assertions.assertEquals("[\"a0\",2]", execute(getScript, key, "$.arr[:-2]"));
            Assertions.assertEquals("[2,true]", execute(getScript, key, "$.arr[1::2]"));
            Assertions.assertEquals("[\"a0\",2,true]", execute(getScript, key, "$.arr[0,1,-1]"));

            Assertions.assertEquals("ReJSON-RL", type(key));
        } finally {
            redisTemplate.delete(key);
        }
    }

    /**
     * 布尔过滤器
     */
    private void bf() {
        String key = "testBF";
        DefaultRedisScript<String> reserveScript = new DefaultRedisScript<>("return redis.call('BF.RESERVE', KEYS[1], KEYS[2], KEYS[3])", String.class);
        DefaultRedisScript<Long> addScript = new DefaultRedisScript<>("return redis.call('BF.ADD', KEYS[1], KEYS[2])", Long.class);
        DefaultRedisScript<Boolean> existsScript = new DefaultRedisScript<>("return redis.call('BF.EXISTS', KEYS[1], KEYS[2])", Boolean.class);
        try {
            // 50条[0, 100)的数字
            Random random = new Random();
            int bound = 100;
            Set<String> set = new HashSet<>(50);
            while (set.size() < 50) {
                set.add(Integer.toHexString(random.nextInt(bound)));
            }

            // 键 错误率 容量
            execute(reserveScript, key, "0.001", "100");
            for (String s : set) {
                execute(addScript, key, s);
            }

            for (String s : set) {
                Assertions.assertTrue(execute(existsScript, key, s));
            }

            Assertions.assertEquals("MBbloom--", type(key));
        } finally {
            redisTemplate.delete(key);
        }
    }

    /**
     * 布谷鸟过滤器
     */
    private void cf() {
        String key = "testCF";
        DefaultRedisScript<String> reserveScript = new DefaultRedisScript<>("return redis.call('CF.RESERVE', KEYS[1], KEYS[2])", String.class);
        DefaultRedisScript<Long> addScript = new DefaultRedisScript<>("return redis.call('CF.ADD', KEYS[1], KEYS[2])", Long.class);
        DefaultRedisScript<Boolean> existsScript = new DefaultRedisScript<>("return redis.call('CF.EXISTS', KEYS[1], KEYS[2])", Boolean.class);
        DefaultRedisScript<Boolean> delScript = new DefaultRedisScript<>("return redis.call('CF.DEL', KEYS[1], KEYS[2])", Boolean.class);
        try {
            // 50条[0, 100)的数字
            Random random = new Random();
            int bound = 100;
            Set<String> set = new HashSet<>(50);
            while (set.size() < 50) {
                set.add(Integer.toHexString(random.nextInt(bound)));
            }

            // 键 容量
            execute(reserveScript, key, "100");
            for (String s : set) {
                execute(addScript, key, s);
            }

            for (String s : set) {
                Assertions.assertTrue(execute(existsScript, key, s));
            }

            // 允许删除
            new HashSet<>(set).stream().limit(10).forEach(s -> {
                set.remove(s);
                execute(delScript, key, s);
            });

            for (String s : set) {
                Assertions.assertTrue(execute(existsScript, key, s));
            }

            Assertions.assertEquals("MBbloomCF", type(key));
        } finally {
            redisTemplate.delete(key);
        }
    }

    /**
     * 近似分位数
     */
    private void tdigest() {
        String key = "testTD";
        DefaultRedisScript<String> createScript = new DefaultRedisScript<>("return redis.call('TDIGEST.CREATE', KEYS[1])", String.class);
        DefaultRedisScript<String> addScript = new DefaultRedisScript<>("return redis.call('TDIGEST.ADD', KEYS[1], KEYS[2])", String.class);
        DefaultRedisScript<String> cdfScript = new DefaultRedisScript<>("return redis.call('TDIGEST.CDF', KEYS[1], KEYS[2])", String.class);
        DefaultRedisScript<Long> rankScript = new DefaultRedisScript<>("return redis.call('TDIGEST.RANK', KEYS[1], KEYS[2])", Long.class);
        DefaultRedisScript<String> quantileScript = new DefaultRedisScript<>("return redis.call('TDIGEST.QUANTILE', KEYS[1], KEYS[2])", String.class);
        try {
            // 键
            execute(createScript, key);

            // 存储1万条 [0, 1000)的数字
            Random random = new Random();
            int bound = 1000;
            try (ExecutorService service = Executors.newVirtualThreadPerTaskExecutor()) {
                for (int i = 0; i < 10_000; i++) {
                    service.submit(() -> execute(addScript, key, Integer.toString(random.nextInt(bound))));
                }
            }

            // 约为 0.6: 根据具体的值获取该值的百分位
            System.out.println(execute(cdfScript, key, Integer.toString((int) Math.round(bound * 0.6))));
            // 约为 10000 * 0.6 = 6000: 根据具体的值获取该值的位置
            System.out.println(execute(rankScript, key, Integer.toString((int) Math.round(bound * 0.6))));
            // 约为 1000 * 0.6 = 600: 根据百分位获取该值
            System.out.println(execute(quantileScript, key, ".6"));

            Assertions.assertEquals("TDIS-TYPE", type(key));
        } finally {
            redisTemplate.delete(key);
        }
    }

    /**
     * 前k位
     */
    private void topk() {
        String key = "testTK";
        DefaultRedisScript<String> reserveScript = new DefaultRedisScript<>("return redis.call('TOPK.RESERVE', KEYS[1], KEYS[2])", String.class);
        DefaultRedisScript<String> addScript = new DefaultRedisScript<>("return redis.call('TOPK.ADD', KEYS[1], KEYS[2])", String.class);
        DefaultRedisScript<List> listScript = new DefaultRedisScript<>("return redis.call('TOPK.LIST', KEYS[1])", List.class);
        DefaultRedisScript<Boolean> queryScript = new DefaultRedisScript<>("return redis.call('TOPK.QUERY', KEYS[1], KEYS[2])", Boolean.class);
        try {
            // 键 数量
            execute(reserveScript, key, "5");
            // 存储[0, 20)该值+1次
            List<String> list = new ArrayList<>();
            for (int i = 0; i < 32; i++) {
                for (int j = 0; j <= i; j++) {
                    list.add(Integer.toHexString(i));
                }
            }
            Collections.shuffle(list);
            list.forEach(s -> execute(addScript, key, s));

            // 最高5条，不准确，但数量越多可能性越大
            System.out.println(execute(listScript, key));
            Assertions.assertFalse(execute(queryScript, key, Integer.toHexString(32)));
            System.out.println(execute(queryScript, key, Integer.toHexString(31)));
            System.out.println(execute(queryScript, key, Integer.toHexString(30)));
            System.out.println(execute(queryScript, key, Integer.toHexString(5)));

            Assertions.assertEquals("TopK-TYPE", type(key));
        } finally {
            redisTemplate.delete(key);
        }
    }

    /**
     * 频率统计
     */
    private void cms() {
        String key = "testCMS";
        DefaultRedisScript<String> initScript = new DefaultRedisScript<>("return redis.call('CMS.INITBYPROB', KEYS[1], KEYS[2], KEYS[3])", String.class);
        DefaultRedisScript<Long> incrScript = new DefaultRedisScript<>("return redis.call('CMS.INCRBY', KEYS[1], KEYS[2], KEYS[3])", Long.class);
        DefaultRedisScript<Long> queryScript = new DefaultRedisScript<>("return redis.call('CMS.QUERY', KEYS[1], KEYS[2])", Long.class);
        try {
            // 键 错误率 确定性
            execute(initScript, key, "0.001", "0.002");
            // 存储[0, 20)该值+1次
            for (int i = 0; i < 32; i++) {
                execute(incrScript, key, Integer.toHexString(i), Integer.toString(i * 1000));
            }

            for (int i = 0; i < 32; i++) {
                long query = execute(queryScript, key, Integer.toHexString(i));
                Assertions.assertTrue(query > i * 1000 - 10 && query < i * 1000 + 10);
            }

            Assertions.assertEquals("CMSk-TYPE", type(key));
        } finally {
            redisTemplate.delete(key);
        }
    }

    /**
     * 时间序列
     */
    private void ts() {
        String key = "testTS";
        String keyAgg = "testTSAgg";
        DefaultRedisScript<String> createScript = new DefaultRedisScript<>("return redis.call('TS.CREATE', KEYS[1])", String.class);
        DefaultRedisScript<Long> addScript = new DefaultRedisScript<>("return redis.call('TS.ADD', KEYS[1], KEYS[2], KEYS[3])", Long.class);
        DefaultRedisScript<String> createRuleScript = new DefaultRedisScript<>("return redis.call('TS.CREATERULE', KEYS[1], KEYS[2], 'AGGREGATION', KEYS[3], KEYS[4])", String.class);
        DefaultRedisScript<List> rangeScript = new DefaultRedisScript<>("return redis.call('TS.RANGE', KEYS[1], KEYS[2], KEYS[3])", List.class);
        try {
            // 键
            execute(createScript, key);
            execute(createScript, keyAgg);
            // 压缩(聚合)
            // 可用类型: avg, sum, min, max, range, count, first, last, std.p, std.s, var.p, var.s and twa
            execute(createRuleScript, key, keyAgg, "sum", "60000");

            Map<Long, String> ts = new HashMap<>();
            long now = LocalDateTime.now().withSecond(0).toEpochSecond(ZoneOffset.ofHours(8)) * 1000;
            for (int i = 0; i < 20; i++) {
                execute(addScript, key, Long.toString(now - i * 10_000), Integer.toString(i));
                ts.put(now - i * 10_000, Integer.toString(i));
            }


            Map<Long, String> sourceRange = ((List<?>) execute(rangeScript, key, "-", "+"))
                    .stream()
                    .map(List.class::cast)
                    .collect(Collectors.toMap(list -> (Long) list.get(0), list -> (String) list.get(1)));
            Assertions.assertEquals(ts, sourceRange);

            Map<Long, String> aggRange = ((List<?>) execute(rangeScript, keyAgg, "-", "+"))
                    .stream()
                    .map(List.class::cast)
                    .collect(Collectors.toMap(list -> (Long) list.get(0), list -> (String) list.get(1)));
            Assertions.assertEquals(
                    Map.of(
                            now - TimeUnit.MINUTES.toMillis(1), Integer.toString(1 + 2 + 3 + 4 + 5 + 6),
                            now - TimeUnit.MINUTES.toMillis(2), Integer.toString(7 + 8 + 9 + 10 + 11 + 12),
                            now - TimeUnit.MINUTES.toMillis(3), Integer.toString(13 + 14 + 15 + 16 + 17 + 18),
                            now - TimeUnit.MINUTES.toMillis(4), Integer.toString(19)
                    ),
                    aggRange
            );

            Assertions.assertEquals("TSDB-TYPE", type(key));
        } finally {
            redisTemplate.delete(keyAgg);
            redisTemplate.delete(key);
        }
    }

}
