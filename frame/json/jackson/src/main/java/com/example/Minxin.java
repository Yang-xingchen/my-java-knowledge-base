package com.example;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class Minxin {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Test
    public void minxin() throws JsonProcessingException {
        MAPPER.addMixIn(Target.class, MinxinTarget.class);
        Target target = new Target();
        target.setId(1);
        target.setName("user");
        String json = MAPPER.writeValueAsString(target);
        // {"userId":1,"userName":"user"}
        System.out.println(json);
        Target target1 = MAPPER.readValue(json, Target.class);
        Assertions.assertEquals(Target.class, target1.getClass());
        Assertions.assertEquals(1, target1.getId());
        Assertions.assertEquals("user", target1.getName());
    }

    public static final class Target {

        private Integer id;
        private String name;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    public static class MinxinTarget {

        @JsonProperty("userId")
        private Integer id;
        @JsonProperty("userName")
        private String name;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

    }

}
