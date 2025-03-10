package com.example;

import com.alibaba.fastjson.JSONReader;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class JsonReader {

    @Test
    public void single() throws IOException {
        String json = "{\"i\":1,\"str\":\"str\",\"list\":[\"str1\",\"str2\"],\"obj\":{\"ii\":2,\"is\":\"objStr\"}}";
        try (ByteArrayInputStream is = new ByteArrayInputStream(json.getBytes());
             InputStreamReader reader = new InputStreamReader(is);
             JSONReader jsonReader = new JSONReader(reader)) {
            // {
            jsonReader.startObject();
            // "i":1
            Assertions.assertEquals("i", jsonReader.readString());
            Assertions.assertEquals(1, jsonReader.readInteger());
            // "str":"str"
            Assertions.assertEquals("str", jsonReader.readString());
            Assertions.assertEquals("str", jsonReader.readString());
            // "list":
            Assertions.assertEquals("list", jsonReader.readString());
            // ["str1","str2"]
            jsonReader.startArray();
            Assertions.assertEquals("str1", jsonReader.readString());
            Assertions.assertEquals("str2", jsonReader.readString());
            jsonReader.endArray();
            // "obj"
            Assertions.assertEquals("obj", jsonReader.readString());
            // {"ii":2,"is":"objStr"}
            jsonReader.startObject();
            Assertions.assertEquals("ii", jsonReader.readString());
            Assertions.assertEquals(2, jsonReader.readInteger());
            Assertions.assertEquals("is", jsonReader.readString());
            Assertions.assertEquals("objStr", jsonReader.readString());
            jsonReader.endObject();

            jsonReader.endObject();
        }
    }

    @Test
    public void multi() throws IOException {
        String json = "{\"str\":\"s1\"}{\"i\":1}{}";
        try (ByteArrayInputStream is = new ByteArrayInputStream(json.getBytes());
             InputStreamReader reader = new InputStreamReader(is);
             JSONReader jsonReader = new JSONReader(reader)) {
            // {"str":"s1"}
            jsonReader.startObject();
            Assertions.assertEquals("str", jsonReader.readString());
            Assertions.assertEquals("s1", jsonReader.readString());
            jsonReader.endObject();
            // {"i":1}
            jsonReader.startObject();
            Assertions.assertEquals("i", jsonReader.readString());
            Assertions.assertEquals(1, jsonReader.readInteger());
            jsonReader.endObject();
            // {}
            jsonReader.startObject();
            jsonReader.endObject();
        }
    }

}
