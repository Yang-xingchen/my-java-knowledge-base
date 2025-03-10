package com.example;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.exc.InvalidDefinitionException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

public class Base {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Test
    public void primitive () throws JsonProcessingException {
        Assertions.assertEquals(1, MAPPER.readValue("1", int.class));
        Assertions.assertEquals(1.23, MAPPER.readValue("1.23", double.class));
        Assertions.assertTrue(MAPPER.readValue("true", boolean.class));
        Assertions.assertThrows(JsonProcessingException.class, () -> MAPPER.readValue("a", char.class));
        Assertions.assertThrows(JsonProcessingException.class, () -> MAPPER.readValue("a", String.class));
        Assertions.assertNull(MAPPER.readValue("null", Object.class));
        Assertions.assertThrows(IllegalArgumentException.class, () -> MAPPER.readValue((String) null, Object.class));
    }

    @Test
    public void entry() throws JsonProcessingException {
        BaseEntry entry = new BaseEntry();
        entry.setI(1);
        entry.setStr("str");
        entry.noGetSet = "noGetSet";
        entry.setTimeUnit(TimeUnit.SECONDS);
        entry.setTra(2);
        entry.pub = 3;
        String json = MAPPER.writeValueAsString(entry);
        // {"i":1,"str":"str","b":false,"timeUnit":"SECONDS","pub":3,"tra":2,"value":"str1"}
        System.out.println(json);
        BaseEntry entry1 = MAPPER.readValue(json, BaseEntry.class);
        Assertions.assertEquals(1, entry1.getI());
        Assertions.assertEquals("str", entry1.getStr());
        Assertions.assertNull(entry1.noGetSet);
        Assertions.assertEquals(TimeUnit.SECONDS, entry1.getTimeUnit());
        Assertions.assertEquals(2, entry1.getTra());
        Assertions.assertEquals(3, entry1.pub);
    }

    @Test
    public void list() throws JsonProcessingException {
        List<String> strings = List.of("str1", "str2");
        String sListJson = MAPPER.writeValueAsString(strings);
        // ["str1","str2"]
        System.out.println(sListJson);
        List<String> strings1 = MAPPER.readValue(sListJson, new TypeReference<>() {});
        Assertions.assertEquals(strings, strings1);

        List<BaseEntry> entries = IntStream.range(0, 3).mapToObj(i -> {
            BaseEntry entry = new BaseEntry();
            entry.setI(i);
            entry.setStr(i + "str");
            return entry;
        }).toList();
        String eListJson = MAPPER.writeValueAsString(entries);
        // [{"i":0,"str":"0str","b":false,"timeUnit":null,"pub":0,"value":"0str0","tra":0},{"i":1,"str":"1str","b":false,"timeUnit":null,"pub":0,"value":"1str1","tra":0},{"i":2,"str":"2str","b":false,"timeUnit":null,"pub":0,"value":"2str2","tra":0}]
        System.out.println(eListJson);
        List<BaseEntry> entries1 = MAPPER.readValue(eListJson, new TypeReference<>() {});
        Assertions.assertEquals(entries, entries1);
        Assertions.assertEquals(BaseEntry.class, entries1.getFirst().getClass());
    }

    @Test
    public void map() throws JsonProcessingException {
        BaseEntry entry = new BaseEntry();
        entry.setI(0);
        entry.setStr("str");
        Map<String, Object> stringMap = Map.of(
                "str", "v1",
                "int", 1,
                "obj", entry,
                "arr", List.of("str", "str2"),
                "double", 1.23,
                "long", Long.MAX_VALUE
        );
        String sMapJson = MAPPER.writeValueAsString(stringMap);
        // {"arr":["str","str2"],"long":9223372036854775807,"str":"v1","int":1,"obj":{"i":0,"str":"str","b":false,"timeUnit":null,"pub":0,"value":"str0","tra":0},"double":1.23}
        System.out.println(sMapJson);
        Map<String, Object> stringMap1 = MAPPER.readValue(sMapJson, new TypeReference<>() {});
        Assertions.assertEquals(LinkedHashMap.class, stringMap1.getClass());
        Assertions.assertEquals(String.class, stringMap1.get("str").getClass());
        Assertions.assertEquals(Integer.class, stringMap1.get("int").getClass());
        Assertions.assertEquals(LinkedHashMap.class, stringMap1.get("obj").getClass());
        Assertions.assertEquals(ArrayList.class, stringMap1.get("arr").getClass());
        Assertions.assertEquals(Double.class, stringMap1.get("double").getClass());
    }

    public static class BaseEntry {
        private Integer i;
        private String str;
        private boolean b;
        private String noGetSet;
        private TimeUnit timeUnit;
        private transient int tra;
        public int pub;

        public Integer getI() {
            return i;
        }

        public void setI(Integer i) {
            this.i = i;
        }

        public String getStr() {
            return str;
        }

        public void setStr(String str) {
            this.str = str;
        }

        public String getValue() {
            return str + i;
        }

        public void setValue(String value) {
            // 会调用
            System.err.println("invoke setValue: " + value);
        }

        public boolean isB() {
            return b;
        }

        public boolean getB() {
            return b;
        }

        public void setB(boolean b) {
            this.b = b;
        }

        public TimeUnit getTimeUnit() {
            return timeUnit;
        }

        public void setTimeUnit(TimeUnit timeUnit) {
            this.timeUnit = timeUnit;
        }

        public int getTra() {
            return tra;
        }

        public void setTra(int tra) {
            this.tra = tra;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            BaseEntry entry = (BaseEntry) o;
            return Objects.equals(i, entry.i) && Objects.equals(str, entry.str);
        }

        @Override
        public int hashCode() {
            return Objects.hash(i, str);
        }
    }

    @Test
    public void record() throws JsonProcessingException {
        RecordEntry entry = new RecordEntry(1L, "user");
        String json = MAPPER.writeValueAsString(entry);
        // {"id":1,"name":"user"}
        System.out.println(json);
        RecordEntry entry1 = MAPPER.readValue(json, RecordEntry.class);
        Assertions.assertEquals(1L, entry1.id());
        Assertions.assertEquals("user", entry1.name());
    }

    public record RecordEntry(Long id, String name) {
    }

    @Test
    public void loop() throws JsonProcessingException {
        LoopEntry loopEntry = new LoopEntry();
        loopEntry.setI(1);
        loopEntry.setLoopEntry(loopEntry);
        Assertions.assertThrows(InvalidDefinitionException.class, () -> MAPPER.writeValueAsString(loopEntry));
        ObjectMapper mapper1 = new ObjectMapper()
                .disable(SerializationFeature.FAIL_ON_SELF_REFERENCES)
                .enable(SerializationFeature.WRITE_SELF_REFERENCES_AS_NULL);
        // {"i":1,"loopEntry":null}
        System.out.println(mapper1.writeValueAsString(loopEntry));
    }

    public static class LoopEntry {

        private Integer i;
        private LoopEntry loopEntry;

        public Integer getI() {
            return i;
        }

        public void setI(Integer i) {
            this.i = i;
        }

        public LoopEntry getLoopEntry() {
            return loopEntry;
        }

        public void setLoopEntry(LoopEntry loopEntry) {
            this.loopEntry = loopEntry;
        }
    }

    @Test
    public void constructor() throws JsonProcessingException {
        ConstructorEntry entry = new ConstructorEntry(1, "s1");
        // {"i1":1,"s1":"s0"}
        String json = MAPPER.writeValueAsString(entry);
        System.out.println(json);
        ConstructorEntry entry1 = MAPPER.readValue(json, ConstructorEntry.class);
        Assertions.assertEquals(1, entry1.i1);
        Assertions.assertEquals("s1", entry1.s1);
    }

    public static class ConstructorEntry {

        private int i1;
        private String s1;

        public ConstructorEntry() {
            System.out.println("no");
            i1 = 0;
            s1 = "s0";
        }

        public ConstructorEntry(@JsonProperty("i1") int i1) {
            System.out.println("int" + i1);
            this.i1 = i1;
            this.s1 = "s0";
        }

        public ConstructorEntry(@JsonProperty("s1") String s1) {
            System.out.println("str" + s1);
            this.i1 = 0;
            this.s1 = s1;
        }

        @JsonCreator
        public ConstructorEntry(@JsonProperty("i1") int i1, @JsonProperty("s1") String s1) {
            System.out.println("int" + i1 + "str" + s1);
            this.i1 = i1;
            this.s1 = s1;
        }

        public int getI1() {
            return i1;
        }

        public void setI1(int i1) {
            this.i1 = i1;
        }

        public String getS1() {
            return s1;
        }

        public void setS1(String s1) {
            this.s1 = s1;
        }
    }

}
