package com.example;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONPath;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

public class JsonPath {

    @Test
    public void base() {
        String json = """
                {"s1": "s1", "num": 1, "arr": ["a0", 2, null, true], "obj": {"obj1": {"s11": "s11"}, "n11": 3}}
                """;
        Assertions.assertEquals("s1", JSONPath.extract(json, "$.s1"));
        Assertions.assertEquals("s1", JSONPath.extract(json, "$['s1']"));
        Assertions.assertEquals(1, JSONPath.extract(json, "$.num"));
        Assertions.assertEquals("a0", JSONPath.extract(json, "$.arr[0]"));
        Assertions.assertEquals(2, JSONPath.extract(json, "$.arr[-3]"));
        Assertions.assertNull(JSONPath.extract(json, "$.arr[2]"));
        Assertions.assertEquals(true, JSONPath.extract(json, "$.arr[3]"));
        Assertions.assertEquals("s11", JSONPath.extract(json, "$.obj.obj1.s11"));
        Assertions.assertEquals("s11", JSONPath.extract(json, "$['obj']['obj1']['s11']"));
        Assertions.assertEquals("s11", JSONPath.extract(json, "$['obj'].obj1['s11']"));
        Assertions.assertEquals(3, JSONPath.extract(json, "$.obj.n11"));

        Assertions.assertEquals(4, JSONPath.extract(json, "$.arr.size()"));
        Assertions.assertEquals(4, JSONPath.extract(json, "$.arr.length()"));


        Assertions.assertEquals(List.of("s1", 1), JSONPath.eval(JSON.parseObject(json), "$['s1','num']"));

        Assertions.assertEquals(List.of("a0", 2), JSONPath.eval(JSON.parseObject(json), "$.arr[:1]"));
        Assertions.assertEquals(List.of("a0", 2), JSONPath.eval(JSON.parseObject(json), "$.arr[:-3]"));
        Assertions.assertEquals(List.of(2, true), JSONPath.eval(JSON.parseObject(json), "$.arr[1:-1:2]"));
        Assertions.assertEquals(List.of("a0", 2, true), JSONPath.eval(JSON.parseObject(json), "$.arr[0,1,-1]"));
    }

    @Test
    public void filter() {
        String json = """
                [
                {"id": 1, "name": "name1"},
                {"id": 2, "name": "name2"},
                {"id": 3, "name": "name3"},
                {"id": 4, "name": "n4"},
                {"id": 5, "name": "n5"}
                ]
                """;
        Assertions.assertEquals(List.of("name1", "name2", "name3"), JSONPath.extract(json, "$[id < 4].name"));
        Assertions.assertEquals(List.of("name2", "name3"), JSONPath.extract(json, "$[id in (2,3)].name"));
//        Assertions.assertEquals(List.of("name2", "name3", "name4"), JSONPath.extract(json, "$[id between 2 and 4].name"));
        Assertions.assertEquals(List.of("name1", "name2", "name3"), JSONPath.extract(json, "$[name like 'name%'].name"));
        Assertions.assertEquals(List.of("n4", "n5"), JSONPath.extract(json, "$[name rlike 'n\\d'].name"));
    }

}
