package com.example;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;

import java.time.*;
import java.util.HashMap;
import java.util.Map;

public class Time {

    private static final ObjectMapper TIMESTAMPS_MAPPER = new ObjectMapper()
            .enable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .registerModule(new JavaTimeModule());
    private static final ObjectMapper MAPPER = new ObjectMapper()
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .registerModule(new JavaTimeModule());

    @Test
    public void time() throws JsonProcessingException {
        Map<String, Object> obj = new HashMap<>();
        obj.put("localDataTime", LocalDateTime.now());
        obj.put("zoneDataTime", ZonedDateTime.now());
        obj.put("instant", Instant.now());
        obj.put("duration", Duration.ofSeconds(30));
        obj.put("zoneId", ZoneId.of("+8"));
        String json = MAPPER.writeValueAsString(obj);
        // {"duration":30.000000000,"zoneId":"+08:00","localDataTime":"2025-03-08T17:28:46.7516271","zoneDataTime":"2025-03-08T17:28:46.7516271+08:00","instant":"2025-03-08T09:28:46.751627100Z"}
        System.out.println(json);
        String tJson = TIMESTAMPS_MAPPER.writeValueAsString(obj);
        // {"duration":30.000000000,"zoneId":"+08:00","localDataTime":[2025,3,8,17,28,46,751627100],"zoneDataTime":1741426126.751627100,"instant":1741426126.751627100}
        System.out.println(tJson);
    }


}
