package com.example;

import com.alibaba.fastjson.JSONPatch;
import org.junit.jupiter.api.Test;

/**
 * <a href="https://jsonpatch.com/">jsonpatch</a>
 */
public class JsonPatch {

    @Test
    public void op() {
        String origin = "{}";

        String add = """
                {"op": "add", "path": "/str", "value": "s1"}
                """;
        origin = JSONPatch.apply(origin, add);
        // {"str":"s1"}
        System.out.println("add: " + origin);
        String addDeep = """
                {"op": "add", "path": "/str1/str11/str111", "value": "s1"}
                """;
        origin = JSONPatch.apply(origin, addDeep);
        // {"str":"s1","str1":{"str11":{"str111":"s1"}}}
        System.out.println("addDeep: " + origin);

        String remove = """
                {"op": "remove", "path": "/str"}
                """;
        origin = JSONPatch.apply(origin, remove);
        // {"str1":{"str11":{"str111":"s1"}}}
        System.out.println("remove: " + origin);
        String removeDeep = """
                {"op": "remove", "path": "/str1/str11/str111"}
                """;
        origin = JSONPatch.apply(origin, removeDeep);
        // {"str1":{"str11":{}}}
        System.out.println("removeDeep: " + origin);

        origin = JSONPatch.apply(origin, add);
        // {"str1":{"str11":{}},"str":"s1"}
        System.out.println("add: " + origin);
        String replace = """
                {"op": "replace", "path": "/str", "value": "s3"}
                """;
        origin = JSONPatch.apply(origin, replace);
        // {"str1":{"str11":{}},"str":"s3"}
        System.out.println("replace: " + origin);
        String replace2 = """
                {"op": "replace", "path": "/str2", "value": "s2"}
                """;
        origin = JSONPatch.apply(origin, replace2);
        // {"str1":{"str11":{}},"str":"s3","str2":"s2"}
        System.out.println("replace2: " + origin);

        String copy = """
                {"op": "copy", "from": "/str", "path": "/str3"}
                """;
        origin = JSONPatch.apply(origin, copy);
        // {"str1":{"str11":{}},"str":"s2","str2":"s2","str3":"s3"}
        System.out.println("copy: " + origin);
        String copy1 = """
                {"op": "copy", "from": "/str4", "path": "/str3"}
                """;
        origin = JSONPatch.apply(origin, copy1);
        // {"str1":{"str11":{}},"str":"s3","str2":"s2"}
        System.out.println("copy1: " + origin);
        String copy2 = """
                {"op": "copy", "from": "/str", "path": "/str2"}
                """;
        origin = JSONPatch.apply(origin, copy2);
        // {"str1":{"str11":{}},"str":"s3","str2":"s3"}
        System.out.println("copy2: " + origin);

        String move = """
                {"op": "move", "from": "str", "path": "/str3"}
                """;
        origin = JSONPatch.apply(origin, move);
        //{"str1":{"str11":{}},"str2":"s3","str3":"s3"}
        System.out.println("move: " + origin);
    }

    @Test
    public void arr() {
        String origin = """
                {arr:[]}
                """;
        String add3 = """
                {"op": "add", "path": "/arr/3", "value": 3}
                """;
        origin = JSONPatch.apply(origin, add3);
        // {"arr":[null,null,null,3]}
        System.out.println("add3: " + origin);

        String add2 = """
                {"op": "add", "path": "/arr/2", "value": 2}
                """;
        origin = JSONPatch.apply(origin, add2);
        // {"arr":[null,null,2,3]}
        System.out.println("add2: " + origin);

        String addN3 = """
                {"op": "add", "path": "/arr/-3", "value": 1}
                """;
        origin = JSONPatch.apply(origin, addN3);
        // {"arr":[null,1,2,3]}
        System.out.println("addN3: " + origin);

        String add0 = """
                {"op": "add", "path": "/arr/0", "value": 0}
                """;
        origin = JSONPatch.apply(origin, add0);
        // {"arr":[0,1,2,3]}
        System.out.println("add0: " + origin);
    }

    @Test
    public void multi() {
        String origin = """
                {}
                """;
        String path = """
                [
                {"op": "add", "path": "/arr", value: []},
                {"op": "add", "path": "/arr/0", value: 0},
                {"op": "add", "path": "/arr/1", value: "s1"},
                {"op": "add", "path": "/s1", value: "s1"},
                ]
                """;
        // {"arr":[0,"s1"],"s1":"s1"}
        System.out.println(JSONPatch.apply(origin, path));
    }

}
