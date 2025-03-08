package com.example;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class Annotation {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Test
    public void write() throws JsonProcessingException {
        BaseEntry entry = new BaseEntry();
        entry.setS1("s1");
        entry.setS2("s2");
        // {"s2":"s2","str1":"s1"}
        String json = MAPPER.writeValueAsString(entry);
        System.out.println(json);
    }

    @Test
    public void read() throws JsonProcessingException {
        BaseEntry entry = MAPPER.readValue("{\"s2\":\"s2\",\"str1\":\"s1\"}", BaseEntry.class);
        Assertions.assertEquals("s1", entry.s1);
        Assertions.assertEquals("s2", entry.s2);
        entry = MAPPER.readValue("{\"str2\":\"s2\",\"str1\":\"s1\"}", BaseEntry.class);
        Assertions.assertEquals("s1", entry.s1);
        Assertions.assertEquals("s2", entry.s2);
    }

    public static class BaseEntry {

        @JsonProperty("str1")
        private String s1;
        @JsonAlias({"s2", "str2"})
        private String s2;
        @JsonIgnore
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
    public void build() throws JsonProcessingException {
        BuildEntry entry = MAPPER.readValue("{\"str2\":\"s2\",\"str1\":\"s1\"}", BuildEntry.class);
        Assertions.assertEquals("s1", entry.str1);
        Assertions.assertEquals("s2", entry.str2);
    }

    @JsonDeserialize(builder = EntryBuild.class)
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

    @JsonPOJOBuilder
    public static class EntryBuild {
        @JsonProperty
        @JsonAlias({"str1", "s1"})
        private String s1;
        @JsonProperty
        @JsonAlias({"str2", "s2"})
        private String s2;

        public void whitS1(String s1) {
            this.s1 = s1;
        }

        public void whitS2(String s2) {
            this.s2 = s2;
        }

        public BuildEntry build() {
            return new BuildEntry(this);
        }
    }

    @Test
    public void staticMethod() throws JsonProcessingException {
        StaticEntry entry = MAPPER.readValue("{\"str2\":\"s2\",\"str1\":\"s1\"}", StaticEntry.class);
        Assertions.assertEquals("s1", entry.str1);
        Assertions.assertEquals("s2", entry.str2);
    }

    public static class StaticEntry {
        private final String str1;
        private final String str2;

        private StaticEntry(String str1, String str2) {
            this.str1 = str1;
            this.str2 = str2;
        }

        public String getStr1() {
            return str1;
        }

        public String getStr2() {
            return str2;
        }

        @JsonCreator
        public static StaticEntry create(@JsonProperty("str1") String s1, @JsonProperty("str2") String s2) {
            return new StaticEntry(s1, s2);
        }

    }

}
