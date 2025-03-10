package com.example;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONType;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class Extends {

    @Test
    public void base() {
        Children1 children1 = new Children1();
        String json1 = JSON.toJSONString(children1, SerializerFeature.WriteClassName);
        // {"@type":"com.example.Extends$Children1"}
        System.out.println(json1);
        Assertions.assertEquals(Children1.class, JSON.parseObject(json1, Root1.class).getClass());
    }

    @Test
    public void name() {
        Children2 children2 = new Children2();
        String json2 = JSON.toJSONString(children2, SerializerFeature.WriteClassName);
        // {"@type":"c2"}
        System.out.println(json2);
        Assertions.assertEquals(Children2.class, JSON.parseObject(json2, Root2.class).getClass());
    }

    public interface Root1 {

    }

    @JSONType(seeAlso = Children2.class)
    public interface Root2 {

    }

    public static class Children1 implements Root1 {

    }

    @JSONType(typeName = "c2")
    public static class Children2 implements Root2 {

    }


}
