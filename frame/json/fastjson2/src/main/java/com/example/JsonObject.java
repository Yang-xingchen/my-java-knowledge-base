package com.example;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class JsonObject {

    @Test
    public void read() {
        String json = "{\"i\":1,\"str\":\"str\",\"list\":[\"str1\",\"str2\"],\"obj\":{\"ii\":2,\"is\":\"objStr\"}}";
        JSONObject jsonObject = JSONObject.parseObject(json);
        Assertions.assertEquals(1, jsonObject.getIntValue("i"));
        Assertions.assertEquals("str", jsonObject.getString("str"));

        JSONArray list = jsonObject.getJSONArray("list");
        Assertions.assertEquals("str1", list.getString(0));
        Assertions.assertEquals("str2", list.getString(1));
        // ["str1","str2"]
        System.out.println(list.toJSONString());

        JSONObject obj = jsonObject.getJSONObject("obj");
        Assertions.assertEquals(2, obj.getIntValue("ii"));
        Assertions.assertEquals("objStr", obj.getString("is"));
        // {"ii":2,"is":"objStr"}
        System.out.println(obj.toJSONString());
    }

    @Test
    public void write() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("i", 1);
        jsonObject.put("str", "str");
        JSONArray jsonArray = new JSONArray();
        jsonArray.add("str1");
        jsonArray.add("str2");
        jsonObject.put("list", jsonArray);
        JSONObject obj = jsonObject.putObject("obj");
        obj.put("ii", 2);
        obj.put("is", "objStr");
        // {"i":1,"str":"str","list":["str1","str2"],"obj":{"ii":2,"is":"objStr"}}
        System.out.println(jsonObject);
    }


}
