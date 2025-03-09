package com.example;

import com.alibaba.fastjson2.JSONObject;
import org.junit.jupiter.api.Test;

import java.time.*;
import java.util.HashMap;
import java.util.Map;

public class Time {

    @Test
    public void time() {
        Map<String, Object> obj = new HashMap<>();
        obj.put("localDataTime", LocalDateTime.now());
        obj.put("zoneDataTime", ZonedDateTime.now());
        obj.put("instant", Instant.now());
        obj.put("duration", Duration.ofSeconds(30));
        obj.put("zoneId", ZoneId.of("+8"));
        String json = JSONObject.toJSONString(obj);
        // {"duration":"PT30S","zoneId":"+08:00","localDataTime":"2025-03-09 15:55:52.805278900","zoneDataTime":"2025-03-09T15:55:52.806412800[Asia/Shanghai]","instant":"2025-03-09T07:55:52.806412800Z"}
        System.out.println(json);
    }


}
