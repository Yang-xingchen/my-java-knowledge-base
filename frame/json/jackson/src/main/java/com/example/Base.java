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
    public void entry() throws JsonProcessingException {
        BaseEntry entry = new BaseEntry();
        entry.setI(1);
        entry.setStr("str");
        entry.noGetSet = "noGetSet";
        entry.setTimeUnit(TimeUnit.SECONDS);
        String json = MAPPER.writeValueAsString(entry);
        // {"i":1,"str":"str","timeUnit":"SECONDS","value":"str1"}
        System.out.println(json);
        BaseEntry entry1 = MAPPER.readValue(json, BaseEntry.class);
        Assertions.assertEquals(1, entry1.getI());
        Assertions.assertEquals("str", entry1.getStr());
        Assertions.assertNull(entry1.noGetSet);
        Assertions.assertEquals(TimeUnit.SECONDS, entry1.getTimeUnit());
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
        // [{"i":0,"str":"0str","timeUnit":null,"value":"0str0"},{"i":1,"str":"1str","timeUnit":null,"value":"1str1"},{"i":2,"str":"2str","timeUnit":null,"value":"2str2"}]
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
        Map<String, Object> stringMap = Map.of("k1", "v1", "k2", 1, "k3", entry, "k4", List.of("str", "str2"));
        String sMapJson = MAPPER.writeValueAsString(stringMap);
        // {"k1":"v1","k2":1,"k3":{"i":0,"str":"str","timeUnit":null,"value":"str0"},"k4":["str","str2"]}
        System.out.println(sMapJson);
        Map<String, Object> stringMap1 = MAPPER.readValue(sMapJson, new TypeReference<>() {});
        Assertions.assertEquals(LinkedHashMap.class, stringMap1.getClass());
        Assertions.assertEquals(String.class, stringMap1.get("k1").getClass());
        Assertions.assertEquals(Integer.class, stringMap1.get("k2").getClass());
        Assertions.assertEquals(LinkedHashMap.class, stringMap1.get("k3").getClass());
        Assertions.assertEquals(ArrayList.class, stringMap1.get("k4").getClass());
    }

    public static class BaseEntry {
        private Integer i;
        private String str;
        private boolean b;
        private String noGetSet;
        private TimeUnit timeUnit;

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
