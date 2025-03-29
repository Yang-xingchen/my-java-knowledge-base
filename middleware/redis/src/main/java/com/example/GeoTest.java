package com.example;

import org.junit.jupiter.api.Assertions;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.geo.*;
import org.springframework.data.redis.connection.DataType;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.GeoOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.data.redis.domain.geo.GeoLocation;

import java.util.Set;
import java.util.stream.Collectors;

public class GeoTest implements CommandLineRunner {

    private final GeoOperations<String, String> ops;
    private final StringRedisTemplate redisTemplate;
    private final ZSetOperations<String, String> zsetOps;

    GeoTest(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
        ops = redisTemplate.opsForGeo();
        zsetOps = redisTemplate.opsForZSet();
    }

    @Override
    public void run(String... args) throws Exception {
        String key = "testGeo";
        try {
            ops.add(key, new Point(0.0, 0.0), "p00");
            ops.add(key, new Point(0.0, 1.0), "p01");
            ops.add(key, new Point(0.0, -1.0), "p0-1");
            ops.add(key, new Point(1.0, 1.0), "p1-1");

            // 两个经纬度间的距离
            Distance distance = ops.distance(key, "p00", "p01");
            System.out.println(distance);

            // 获取指定点圆形范围内的坐标
            GeoResults<RedisGeoCommands.GeoLocation<String>> radius = ops.radius(key, "p00", distance.add(new Distance(1, Metrics.MILES)));
            Set<String> radiusRes = radius.getContent().stream().map(GeoResult::getContent).map(GeoLocation::getName).collect(Collectors.toSet());
            Assertions.assertEquals(Set.of("p00", "p01", "p0-1"), radiusRes);

            // 获取指定位置圆形范围内的坐标
            GeoResults<RedisGeoCommands.GeoLocation<String>> radius2 = ops.radius(key, new Circle(new Point(0.0, 0.0), distance.add(new Distance(1, Metrics.MILES))));
            Set<String> radiusRes2 = radius2.getContent().stream().map(GeoResult::getContent).map(GeoLocation::getName).collect(Collectors.toSet());
            Assertions.assertEquals(Set.of("p00", "p01", "p0-1"), radiusRes2);

            // 实际类型
            Set<String> range = zsetOps.range(key, 0, -1);
            Assertions.assertEquals(Set.of("p00", "p01", "p0-1", "p1-1"), range);

            Assertions.assertEquals(DataType.ZSET, redisTemplate.type(key));
        } finally {
            redisTemplate.delete(key);
        }
    }

}
