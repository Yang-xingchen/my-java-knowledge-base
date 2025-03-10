package com.example;

import com.alibaba.fastjson.JSON;
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
        String json = JSON.toJSONString(obj);
        // {"duration":"PT30S","zoneId":"+08:00","localDataTime":"2025-03-10T15:15:39.874080100","zoneDataTime":"2025-03-10T15:15:39.875080100+08:00[Asia/Shanghai]","instant":"2025-03-10T07:15:39.875080100Z"}
        System.out.println(json);
    }


}
