package com.example;

import com.alibaba.fastjson2.JSONReader;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class JsonReader {

    @Test
    public void single() {
        String json = "{\"i\":1,\"str\":\"str\",\"list\":[\"str1\",\"str2\"],\"obj\":{\"ii\":2,\"is\":\"objStr\"}}";
        try (JSONReader jsonReader = JSONReader.of(json)) {
            // {
            Assertions.assertTrue(jsonReader.nextIfObjectStart());
            // "i":1
            Assertions.assertEquals("i", jsonReader.readFieldName());
            Assertions.assertEquals(1, jsonReader.readInt32Value());
            // "str":"str"
            Assertions.assertEquals("str", jsonReader.readFieldName());
            Assertions.assertEquals("str", jsonReader.readString());
            // "list":
            Assertions.assertEquals("list", jsonReader.readFieldName());
            // ["str1","str2"]
            Assertions.assertTrue(jsonReader.nextIfArrayStart());
            Assertions.assertEquals("str1", jsonReader.readString());
            Assertions.assertEquals("str2", jsonReader.readString());
            Assertions.assertTrue(jsonReader.nextIfArrayEnd());
            // "obj"
            Assertions.assertEquals("obj", jsonReader.readFieldName());
            // {"ii":2,"is":"objStr"}
            Assertions.assertTrue(jsonReader.nextIfObjectStart());
            Assertions.assertEquals("ii", jsonReader.readFieldName());
            Assertions.assertEquals(2, jsonReader.readInt32Value());
            Assertions.assertEquals("is", jsonReader.readFieldName());
            Assertions.assertEquals("objStr", jsonReader.readString());
            Assertions.assertTrue(jsonReader.nextIfObjectEnd());

            Assertions.assertTrue(jsonReader.nextIfObjectEnd());
        }
    }

    @Test
    public void multi() {
        String json = "{\"str\":\"s1\"}{\"i\":1}{}";
        try (JSONReader jsonReader = JSONReader.of(json)) {
            // {"str":"s1"}
            Assertions.assertTrue(jsonReader.nextIfObjectStart());
            Assertions.assertEquals("str", jsonReader.readFieldName());
            Assertions.assertEquals("s1", jsonReader.readString());
            Assertions.assertTrue(jsonReader.nextIfObjectEnd());
            // {"i":1}
            Assertions.assertTrue(jsonReader.nextIfObjectStart());
            Assertions.assertEquals("i", jsonReader.readFieldName());
            Assertions.assertEquals(1, jsonReader.readInt32Value());
            Assertions.assertTrue(jsonReader.nextIfObjectEnd());
            // {}
            Assertions.assertTrue(jsonReader.nextIfObjectStart());
            Assertions.assertTrue(jsonReader.nextIfObjectEnd());
        }
    }

}
