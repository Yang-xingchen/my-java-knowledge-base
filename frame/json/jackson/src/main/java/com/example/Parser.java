package com.example;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public class Parser {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Test
    public void single() throws IOException {
        String json = "{\"i\":1,\"str\":\"str\",\"list\":[\"str1\",\"str2\"],\"obj\":{\"ii\":2,\"is\":\"objStr\"}}";
        try (JsonParser parser = MAPPER.createParser(json)) {
            // {
            Assertions.assertEquals(JsonToken.START_OBJECT, parser.nextToken());
            Assertions.assertNull(parser.currentName());
            // "i":1
            Assertions.assertEquals(JsonToken.FIELD_NAME, parser.nextToken());
            Assertions.assertEquals("i", parser.currentName());
            Assertions.assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
            Assertions.assertEquals(1, parser.readValueAs(Integer.class));
            // "str":"str"
            Assertions.assertEquals(JsonToken.FIELD_NAME, parser.nextToken());
            Assertions.assertEquals("str", parser.currentName());
            Assertions.assertEquals(JsonToken.VALUE_STRING, parser.nextToken());
            Assertions.assertEquals("str", parser.readValueAs(String.class));
            // "list":
            Assertions.assertEquals(JsonToken.FIELD_NAME, parser.nextToken());
            Assertions.assertEquals("list", parser.currentName());
            // ["str1","str2"]
            Assertions.assertEquals(JsonToken.START_ARRAY, parser.nextToken());
            Assertions.assertEquals("list", parser.currentName());
            Assertions.assertEquals(JsonToken.VALUE_STRING, parser.nextToken());
            Assertions.assertEquals("str1", parser.readValueAs(String.class));
            Assertions.assertEquals(JsonToken.VALUE_STRING, parser.nextToken());
            Assertions.assertEquals("str2", parser.readValueAs(String.class));
            Assertions.assertEquals(JsonToken.END_ARRAY, parser.nextToken());
            Assertions.assertEquals("list", parser.currentName());
            // "obj"
            Assertions.assertEquals(JsonToken.FIELD_NAME, parser.nextToken());
            Assertions.assertEquals("obj", parser.currentName());
            // {"ii":2,"is":"objStr"}
            Assertions.assertEquals(JsonToken.START_OBJECT, parser.nextToken());
            Assertions.assertEquals("obj", parser.currentName());
            Assertions.assertEquals(JsonToken.FIELD_NAME, parser.nextToken());
            Assertions.assertEquals("ii", parser.currentName());
            Assertions.assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
            Assertions.assertEquals(2, parser.readValueAs(Integer.class));
            Assertions.assertEquals(JsonToken.FIELD_NAME, parser.nextToken());
            Assertions.assertEquals("is", parser.currentName());
            Assertions.assertEquals(JsonToken.VALUE_STRING, parser.nextToken());
            Assertions.assertEquals("objStr", parser.readValueAs(String.class));
            Assertions.assertEquals(JsonToken.END_OBJECT, parser.nextToken());
            Assertions.assertEquals("obj", parser.currentName());
            // }
            Assertions.assertEquals(JsonToken.END_OBJECT, parser.nextToken());
            Assertions.assertNull(parser.currentName());

            Assertions.assertNull(parser.nextToken());
        }
    }

    @Test
    public void multi() throws IOException {
        String json = "{\"str\":\"s1\"}{\"i\":1}{}";
        try (JsonParser parser = MAPPER.createParser(json)) {
            // {"str":"s1"}
            Assertions.assertEquals(JsonToken.START_OBJECT, parser.nextToken());
            Assertions.assertNull(parser.currentName());
            Assertions.assertEquals(JsonToken.FIELD_NAME, parser.nextToken());
            Assertions.assertEquals("str", parser.currentName());
            Assertions.assertEquals(JsonToken.VALUE_STRING, parser.nextToken());
            Assertions.assertEquals("s1", parser.readValueAs(String.class));
            Assertions.assertEquals(JsonToken.END_OBJECT, parser.nextToken());
            Assertions.assertNull(parser.currentName());
            // {"i":1}
            Assertions.assertEquals(JsonToken.START_OBJECT, parser.nextToken());
            Assertions.assertNull(parser.currentName());
            Assertions.assertEquals(JsonToken.FIELD_NAME, parser.nextToken());
            Assertions.assertEquals("i", parser.currentName());
            Assertions.assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
            Assertions.assertEquals(1, parser.readValueAs(Integer.class));
            Assertions.assertEquals(JsonToken.END_OBJECT, parser.nextToken());
            Assertions.assertNull(parser.currentName());
            // {}
            Assertions.assertEquals(JsonToken.START_OBJECT, parser.nextToken());
            Assertions.assertNull(parser.currentName());
            Assertions.assertEquals(JsonToken.END_OBJECT, parser.nextToken());
            Assertions.assertNull(parser.currentName());
        }
    }

}
