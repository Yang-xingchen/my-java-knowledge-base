package com.example;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.annotation.JSONPOJOBuilder;
import com.alibaba.fastjson.annotation.JSONType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class Annotation {

    @Test
    public void write() {
        BaseEntry entry = new BaseEntry();
        entry.setS1("s1");
        entry.setS2("s2");
        // {"s2":"s2","str1":"s1"}
        String json = JSON.toJSONString(entry);
        System.out.println(json);
    }

    @Test
    public void read() {
        BaseEntry entry = JSON.parseObject("{\"s2\":\"s2\",\"str1\":\"s1\"}", BaseEntry.class);
        Assertions.assertEquals("s1", entry.s1);
        Assertions.assertEquals("s2", entry.s2);
        entry = JSON.parseObject("{\"str2\":\"s2\",\"str1\":\"s1\"}", BaseEntry.class);
        Assertions.assertEquals("s1", entry.s1);
        Assertions.assertEquals("s2", entry.s2);
    }

    public static class BaseEntry {

        @JSONField(name = "str1")
        private String s1;
        @JSONField(alternateNames = {"s2", "str2"})
        private String s2;
        @JSONField(serialize = false, deserialize = false)
        private String s3;

        public String getS1() {
            return s1;
        }

        public void setS1(String s1) {
            this.s1 = s1;
        }

        public String getS2() {
            return s2;
        }

        public void setS2(String s2) {
            this.s2 = s2;
        }

        public String getS3() {
            return s3;
        }

        public void setS3(String s3) {
            this.s3 = s3;
        }
    }

    @Test
    public void build() {
        BuildEntry entry = JSON.parseObject("{\"str2\":\"s2\",\"str1\":\"s1\"}", BuildEntry.class);
        Assertions.assertEquals("s1", entry.str1);
        Assertions.assertEquals("s2", entry.str2);
    }

    @JSONType(builder = EntryBuild.class)
    public static class BuildEntry {
        private final String str1;
        private final String str2;

        private BuildEntry(EntryBuild build) {
            this.str1 = build.s1;
            this.str2 = build.s2;
        }

        public String getStr1() {
            return str1;
        }

        public String getStr2() {
            return str2;
        }
    }

    @JSONPOJOBuilder
    public static class EntryBuild {
        private String s1;
        private String s2;

        public EntryBuild withStr1(String s1) {
            this.s1 = s1;
            return this;
        }

        @JSONField(alternateNames = {"str2", "s2"})
        public EntryBuild withS2(String s2) {
            this.s2 = s2;
            return this;
        }

        public BuildEntry build() {
            return new BuildEntry(this);
        }
    }

}
