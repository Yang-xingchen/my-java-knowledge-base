package com.example;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Map;

public class Extends {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Test
    public void cls() throws JsonProcessingException {
        Children1 children1 = new Children1();
        String json1 = MAPPER.writeValueAsString(children1);
        // {"@class":"com.example.Extends$Children1"}
        System.out.println(json1);
        Assertions.assertEquals(Children1.class, MAPPER.readValue(json1, Root1.class).getClass());
    }

    @Test
    public void name() throws JsonProcessingException {
        Children2 children2 = new Children2();
        String json2 = MAPPER.writeValueAsString(children2);
        // {"@type":"c2"}
        System.out.println(json2);
        Assertions.assertEquals(Children2.class, MAPPER.readValue(json2, Root2.class).getClass());
    }

    @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
    public interface Root1 {

    }
    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME)
    @JsonSubTypes({
            @JsonSubTypes.Type(value = Children2.class, name = "c2")
    })
    public interface Root2 {

    }

    public static class Children1 implements Root1 {

    }

    public static class Children2 implements Root2 {

    }

}
