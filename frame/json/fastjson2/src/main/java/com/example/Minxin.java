package com.example;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.annotation.JSONField;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class Minxin {

    @Test
    public void minxin() {
        JSON.mixIn(Target.class, MinxinTarget.class);
        Target target = new Target();
        target.setId(1);
        target.setName("user");
        String json = JSON.toJSONString(target);
        // {"userId":1,"userName":"user"}
        System.out.println(json);
        Target target1 = JSON.parseObject(json, Target.class);
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

        @JSONField(name = "userId")
        private Integer id;
        @JSONField(name = "userName")
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
