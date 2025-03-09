package com.example;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONException;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.TypeReference;
import com.alibaba.fastjson2.annotation.JSONCreator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

public class Base {

    @Test
    public void entry() {
        BaseEntry entry = new BaseEntry();
        entry.setI(1);
        entry.setStr("str");
        entry.noGetSet = "noGetSet";
        entry.setTimeUnit(TimeUnit.SECONDS);
        entry.setTra(2);
        entry.pub = 3;
        String json = JSONObject.toJSONString(entry);
        // {"b":false,"i":1,"pub":3,"str":"str","timeUnit":"SECONDS","value":"str1"}
        System.out.println(json);
        BaseEntry entry1 = JSONObject.parseObject(json, BaseEntry.class);
        Assertions.assertEquals(1, entry1.getI());
        Assertions.assertEquals("str", entry1.getStr());
        Assertions.assertNull(entry1.noGetSet);
        Assertions.assertEquals(TimeUnit.SECONDS, entry1.getTimeUnit());
        Assertions.assertEquals(0, entry1.getTra());
        Assertions.assertEquals(3, entry1.pub);
    }

    @Test
    public void list() {
        List<String> strings = List.of("str1", "str2");
        String sListJson = JSONObject.toJSONString(strings);
        // ["str1","str2"]
        System.out.println(sListJson);
        List<String> strings1 = JSONObject.parseObject(sListJson, new TypeReference<>() {});
        Assertions.assertEquals(strings, strings1);

        List<BaseEntry> entries = IntStream.range(0, 3).mapToObj(i -> {
            BaseEntry entry = new BaseEntry();
            entry.setI(i);
            entry.setStr(i + "str");
            return entry;
        }).toList();
        String eListJson = JSONObject.toJSONString(entries);
        // [{"b":false,"i":0,"pub":0,"str":"0str","value":"0str0"},{"b":false,"i":1,"pub":0,"str":"1str","value":"1str1"},{"b":false,"i":2,"pub":0,"str":"2str","value":"2str2"}]
        System.out.println(eListJson);
        List<BaseEntry> entries1 = JSONObject.parseObject(eListJson, new TypeReference<>() {});
        Assertions.assertEquals(entries, entries1);
        Assertions.assertEquals(BaseEntry.class, entries1.getFirst().getClass());
    }

    @Test
    public void map() {
        BaseEntry entry = new BaseEntry();
        entry.setI(0);
        entry.setStr("str");
        Map<String, Object> stringMap = Map.of(
                "str", "v1",
                "int", 1,
                "obj", entry,
                "arr", List.of("str", "str2"),
                "double", 1.23
        );
        String sMapJson = JSONObject.toJSONString(stringMap);
        // {"int":1,"double":1.23,"arr":["str","str2"],"obj":{"b":false,"i":0,"pub":0,"str":"str","value":"str0"},"str":"v1"}
        System.out.println(sMapJson);
        Map<String, Object> stringMap1 = JSONObject.parseObject(sMapJson, new TypeReference<>() {});
        Assertions.assertEquals(HashMap.class, stringMap1.getClass());
        Assertions.assertEquals(String.class, stringMap1.get("str").getClass());
        Assertions.assertEquals(Integer.class, stringMap1.get("int").getClass());
        Assertions.assertEquals(JSONObject.class, stringMap1.get("obj").getClass());
        Assertions.assertEquals(JSONArray.class, stringMap1.get("arr").getClass());
        Assertions.assertEquals(BigDecimal.class, stringMap1.get("double").getClass());
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
            // 不会调用
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
    public void loop() {
        LoopEntry loopEntry = new LoopEntry();
        loopEntry.setI(1);
        loopEntry.setLoopEntry(loopEntry);
        Assertions.assertThrows(JSONException.class, () -> JSONObject.toJSONString(loopEntry));
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
    public void constructor() {
        ConstructorEntry entry = new ConstructorEntry(1, "s1");
        // {"i1":1,"s1":"s1"}
        String json = JSONObject.toJSONString(entry);
        System.out.println(json);
        ConstructorEntry entry1 = JSONObject.parseObject(json, ConstructorEntry.class);
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

        public ConstructorEntry(int i1) {
            System.out.println("int" + i1);
            this.i1 = i1;
            this.s1 = "s0";
        }

        public ConstructorEntry(String s1) {
            System.out.println("str" + s1);
            this.i1 = 0;
            this.s1 = s1;
        }

        public ConstructorEntry(int i1, String s1) {
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
