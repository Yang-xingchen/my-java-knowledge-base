package com.example;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class Node {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Test
    public void read() throws JsonProcessingException {
        String json = "{\"i\":1,\"str\":\"str\",\"list\":[\"str1\",\"str2\"],\"obj\":{\"ii\":2,\"is\":\"objStr\"}}";
        JsonNode jsonNode = MAPPER.readTree(json);
        Assertions.assertEquals(1, jsonNode.get("i").asInt());
        Assertions.assertEquals("str", jsonNode.get("str").asText());

        ArrayNode list = jsonNode.withArrayProperty("list");
        Assertions.assertEquals("str1", list.get(0).asText());
        Assertions.assertEquals("str2", list.get(1).asText());
        Assertions.assertEquals("", list.asText());
        // ["str1","str2"]
        System.out.println(MAPPER.writeValueAsString(list));

        JsonNode obj = jsonNode.get("obj");
        Assertions.assertEquals(2, obj.get("ii").asInt());
        Assertions.assertEquals("objStr", obj.get("is").asText());
        Assertions.assertEquals("", obj.asText());
        // {"ii":2,"is":"objStr"}
        System.out.println(MAPPER.writeValueAsString(obj));
    }

    @Test
    public void at() throws JsonProcessingException {
        String json = "{\"i\":1,\"str\":\"str\",\"list\":[\"str1\",\"str2\"],\"obj\":{\"ii\":2,\"is\":\"objStr\"}}";
        JsonNode jsonNode = MAPPER.readTree(json);
        Assertions.assertEquals(1, jsonNode.at("/i").asInt());
        Assertions.assertEquals("str", jsonNode.at("/str").asText());
        Assertions.assertEquals("str1", jsonNode.at("/list/0").asText());
        Assertions.assertEquals("str2", jsonNode.at("/list/1").asText());
        Assertions.assertEquals(2, jsonNode.at("/obj/ii").asInt());
        Assertions.assertEquals("objStr", jsonNode.at("/obj/is").asText());
    }

    @Test
    public void write() {
        ObjectNode objectNode = MAPPER.createObjectNode();
        objectNode.put("i", 1);
        objectNode.put("str", "str");
        ArrayNode arrayNode = objectNode.putArray("list");
        arrayNode.add("str1");
        arrayNode.add("str2");
        ObjectNode obj = objectNode.putObject("obj");
        obj.put("ii", 2);
        obj.put("is", "objStr");
        // {"i":1,"str":"str","list":["str1","str2"],"obj":{"ii":2,"is":"objStr"}}
        System.out.println(objectNode);
    }

    @Test
    public void with() {
        ObjectNode objectNode = MAPPER.createObjectNode();
        objectNode.put("i", 1);
        objectNode.put("str", "str");
        objectNode.putArray("list");
        objectNode.withArray("list").add("str1");
        objectNode.withArray("list").add("str2");
        objectNode.withObject("obj").put("ii", 2);
        objectNode.withObject("obj").put("is", "objStr");
        // {"i":1,"str":"str","list":["str1","str2"],"obj":{"ii":2,"is":"objStr"}}
        System.out.println(objectNode);
    }

}
