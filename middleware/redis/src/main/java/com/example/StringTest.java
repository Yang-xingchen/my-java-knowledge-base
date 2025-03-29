package com.example;

import org.junit.jupiter.api.Assertions;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.redis.connection.BitFieldSubCommands;
import org.springframework.data.redis.connection.DataType;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
        bitMap();
        bitField();
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

    private static String stringBytesToHex(String s) {
        if (s == null) {
            return null;
        }
        Stream.Builder<Byte> builder = Stream.builder();
        for (byte b : s.getBytes()) {
            builder.add(b);
        }
        return builder.build().mapToInt(Byte::toUnsignedInt).mapToObj(Integer::toHexString).collect(Collectors.joining());
    }

    /**
     * 位图
     */
    private void bitMap() {
        String key = "testBitMap";
        try {
            ops.setBit(key, 0, false);
            ops.setBit(key, 1, true);

            Assertions.assertEquals(Boolean.FALSE, ops.getBit(key, 0));
            Assertions.assertEquals(Boolean.TRUE, ops.getBit(key, 1));
            // 默认为false
            Assertions.assertEquals(Boolean.FALSE, ops.getBit(key, 2));

            // 0 1 2 3 4 5 6 7
            // 0 1 0 0 0 0 0 1
            ops.setBit(key, 7, true);
            String s = ops.get(key);
            Assertions.assertEquals("41", stringBytesToHex(s));

            Assertions.assertEquals(DataType.STRING, redisTemplate.type(key));
        } finally {
            redisTemplate.delete(key);
        }
    }

    /**
     * 位域
     */
    private void bitField() {
        String key = "testBitField";
        try {
            //  0  8 16 24 32 40 48 56 64 72 80 88 96 104 112 120
            //  1| 2| 3|          4|                            5|
            ops.bitField(key, BitFieldSubCommands.create(
                    BitFieldSubCommands.BitFieldSet.create(BitFieldSubCommands.BitFieldType.INT_8, BitFieldSubCommands.Offset.offset(0), 1),
                    BitFieldSubCommands.BitFieldSet.create(BitFieldSubCommands.BitFieldType.INT_8, BitFieldSubCommands.Offset.offset(8), 2),
                    BitFieldSubCommands.BitFieldSet.create(BitFieldSubCommands.BitFieldType.INT_8, BitFieldSubCommands.Offset.offset(16), 3),
                    BitFieldSubCommands.BitFieldSet.create(BitFieldSubCommands.BitFieldType.INT_32, BitFieldSubCommands.Offset.offset(24), 4),
                    BitFieldSubCommands.BitFieldSet.create(BitFieldSubCommands.BitFieldType.INT_64, BitFieldSubCommands.Offset.offset(56), 5)
            ));

            List<Long> get = ops.bitField(key, BitFieldSubCommands.create(
                    BitFieldSubCommands.BitFieldGet.create(BitFieldSubCommands.BitFieldType.INT_8, BitFieldSubCommands.Offset.offset(0)),
                    BitFieldSubCommands.BitFieldGet.create(BitFieldSubCommands.BitFieldType.INT_8, BitFieldSubCommands.Offset.offset(8)),
                    BitFieldSubCommands.BitFieldGet.create(BitFieldSubCommands.BitFieldType.INT_8, BitFieldSubCommands.Offset.offset(16)),
                    BitFieldSubCommands.BitFieldGet.create(BitFieldSubCommands.BitFieldType.INT_32, BitFieldSubCommands.Offset.offset(24)),
                    BitFieldSubCommands.BitFieldGet.create(BitFieldSubCommands.BitFieldType.INT_64, BitFieldSubCommands.Offset.offset(56))
            ));
            Assertions.assertEquals(List.of(1L, 2L, 3L, 4L, 5L), get);
            Assertions.assertEquals("123000400000005", stringBytesToHex(ops.get(key)));

            ops.bitField(key, BitFieldSubCommands.create(
                    BitFieldSubCommands.BitFieldIncrBy.create(BitFieldSubCommands.BitFieldType.INT_8, BitFieldSubCommands.Offset.offset(0), 1),
                    BitFieldSubCommands.BitFieldIncrBy.create(BitFieldSubCommands.BitFieldType.INT_8, BitFieldSubCommands.Offset.offset(8), 1),
                    BitFieldSubCommands.BitFieldIncrBy.create(BitFieldSubCommands.BitFieldType.INT_8, BitFieldSubCommands.Offset.offset(16), 1),
                    BitFieldSubCommands.BitFieldIncrBy.create(BitFieldSubCommands.BitFieldType.INT_32, BitFieldSubCommands.Offset.offset(24), 1),
                    BitFieldSubCommands.BitFieldIncrBy.create(BitFieldSubCommands.BitFieldType.INT_64, BitFieldSubCommands.Offset.offset(56), 1)
            ));

            List<Long> get1 = ops.bitField(key, BitFieldSubCommands.create(
                    BitFieldSubCommands.BitFieldGet.create(BitFieldSubCommands.BitFieldType.INT_8, BitFieldSubCommands.Offset.offset(0)),
                    BitFieldSubCommands.BitFieldGet.create(BitFieldSubCommands.BitFieldType.INT_8, BitFieldSubCommands.Offset.offset(8)),
                    BitFieldSubCommands.BitFieldGet.create(BitFieldSubCommands.BitFieldType.INT_8, BitFieldSubCommands.Offset.offset(16)),
                    BitFieldSubCommands.BitFieldGet.create(BitFieldSubCommands.BitFieldType.INT_32, BitFieldSubCommands.Offset.offset(24)),
                    BitFieldSubCommands.BitFieldGet.create(BitFieldSubCommands.BitFieldType.INT_64, BitFieldSubCommands.Offset.offset(56))
            ));
            Assertions.assertEquals(List.of(2L, 3L, 4L, 5L, 6L), get1);
            Assertions.assertEquals("234000500000006", stringBytesToHex(ops.get(key)));

            Assertions.assertEquals(DataType.STRING, redisTemplate.type(key));
        } finally {
            redisTemplate.delete(key);
        }
    }

}
