package com.example;

import org.junit.jupiter.api.Assertions;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.domain.Range;
import org.springframework.data.redis.connection.DataType;
import org.springframework.data.redis.connection.stream.*;
import org.springframework.data.redis.connection.stream.Record;
import org.springframework.data.redis.core.StreamOperations;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class StreamTest implements CommandLineRunner {

    private final StreamOperations<String, Object, Object> ops;
    private final StringRedisTemplate redisTemplate;

    StreamTest(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
        ops = redisTemplate.opsForStream();
    }

    @Override
    public void run(String... args) throws Exception {
        base();
        group();
    }

    private void base() {
        String key = "streamTestBase";
        try {
            Assertions.assertNotNull(ops.add(StreamRecords.newRecord().in(key).ofStrings(Map.of("data", "v1"))));
            Assertions.assertNotNull(ops.add(StreamRecords.newRecord().in(key).ofStrings(Map.of("data", "v2"))));

            Assertions.assertEquals(2, ops.size(key));
            // 读不提交
            List<MapRecord<String, Object, Object>> range = ops.range(key, Range.open("-", "+"));
            Assertions.assertEquals(2, range.size());
            List<MapRecord<String, Object, Object>> range1 = ops.range(key, Range.open("-", "+"));
            Assertions.assertEquals(2, range1.size());

            // 读取数据
            Assertions.assertEquals("v1", range.get(0).getValue().get("data"));
            Assertions.assertEquals("v2", range.get(1).getValue().get("data"));

            // 删除消息
            Assertions.assertEquals(1, ops.delete(key, range.get(0).getId()));

            List<MapRecord<String, Object, Object>> range2 = ops.range(key, Range.open("-", "+"));
            Assertions.assertEquals(1, range2.size());
            Assertions.assertEquals("v2", range2.get(0).getValue().get("data"));

            Assertions.assertNotNull(ops.add(StreamRecords.newRecord().in(key).ofStrings(Map.of("data", "v3", "data2", "3"))));
            Assertions.assertNotNull(ops.add(StreamRecords.newRecord().in(key).ofStrings(Map.of("data", "v4", "data2", "4"))));

            List<MapRecord<String, Object, Object>> range3 = ops.range(key, Range.open("-", "+"));
            Assertions.assertEquals(3, range3.size());

            // 读不提交
            List<MapRecord<String, Object, Object>> read = ops.read(StreamOffset.create(key, ReadOffset.from("0")));
            Assertions.assertEquals(3, read.size());
            List<MapRecord<String, Object, Object>> read1 = ops.read(StreamOffset.create(key, ReadOffset.from("0")));
            Assertions.assertEquals(3, read1.size());

            // 读一条消息
            List<MapRecord<String, Object, Object>> read2 = ops.read(StreamReadOptions.empty().count(1), StreamOffset.create(key, ReadOffset.from("2")));
            Assertions.assertEquals(1, read2.size());
            Assertions.assertEquals("v2", read2.get(0).getValue().get("data"));

            Assertions.assertEquals(DataType.STREAM, redisTemplate.type(key));
        } finally {
            redisTemplate.delete(key);
        }
    }

    private void group() throws Exception {
        String key = "streamTestGroup";
        try {
            ops.createGroup(key, "g1");
            ops.createGroup(key, "g2");

            ops.add(key, Map.of("data", "v1"));

            StreamReadOptions readOptions = StreamReadOptions.empty().count(1);
            StreamOffset<String> streamOffset = StreamOffset.create(key, ReadOffset.lastConsumed());
            List<MapRecord<String, Object, Object>> g1c1 = ops.read(Consumer.from("g1", "c1"), readOptions, streamOffset);
            List<MapRecord<String, Object, Object>> g1c2 = ops.read(Consumer.from("g1", "c2"), readOptions, streamOffset);
            List<MapRecord<String, Object, Object>> g2c1 = ops.read(Consumer.from("g2", "c1"), readOptions, streamOffset);

            ops.add(key, Map.of("data", "v1"));

            Assertions.assertEquals(1, g1c1.size() + g1c2.size());
            Assertions.assertEquals(1, g2c1.size());

            Assertions.assertEquals("v1", Stream.concat(g1c1.stream(), g1c2.stream()).map(Record::getValue).map(map -> map.get("data")).findAny().get());
            Assertions.assertEquals("v1", g2c1.get(0).getValue().get("data"));

            Assertions.assertEquals(DataType.STREAM, redisTemplate.type(key));
        } finally {
            redisTemplate.delete(key);
        }
    }

}
