package com.example;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONReader;
import com.alibaba.fastjson2.JSONWriter;
import com.alibaba.fastjson2.annotation.JSONType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class Extends {

    @Test
    public void base() {
        Children1 children1 = new Children1();
        String json1 = JSON.toJSONString(children1, JSONWriter.Feature.WriteClassName);
        // {"@type":"com.example.Extends$Children1"}
        System.out.println(json1);
        Assertions.assertEquals(Children1.class, JSON.parseObject(json1, Root1.class, JSONReader.Feature.SupportAutoType).getClass());
    }

    @Test
    public void name() {
        Children2 children2 = new Children2();
        String json2 = JSON.toJSONString(children2);
        // {"@type":"c2"}
        System.out.println(json2);
        Assertions.assertEquals(Children2.class, JSON.parseObject(json2, Root2.class).getClass());
    }

    public interface Root1 {

    }

    @JSONType(seeAlso = Children2.class, serializeFeatures = JSONWriter.Feature.WriteClassName)
    public interface Root2 {

    }

    public static class Children1 implements Root1 {

    }

    @JSONType(typeName = "c2")
    public static class Children2 implements Root2 {

    }


}
