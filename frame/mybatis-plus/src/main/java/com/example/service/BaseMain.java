package com.example.service;

import com.example.entry.Gender;
import com.example.entry.User;
import com.example.mapper.UserMapper;
import org.junit.jupiter.api.Assertions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;

public class BaseMain implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(BaseMain.class);

    @Autowired
    private UserMapper userMapper;

    @Override
    public void run(String... args) throws Exception {
        log.info("==================== {} ====================", getClass().getSimpleName());
        Long uid = save();
        find(uid);
        update(uid);
        delete(uid);
    }

    private Long save() {
        User user = new User();
        user.setName("user");
        user.setGender(Gender.MAN);
        Assertions.assertEquals(1, userMapper.insert(user));
        log.info("save: uid: {}", user.getId());
        Assertions.assertNotNull(user.getId());
        return user.getId();
    }

    private void find(Long uid) {
        User user = userMapper.selectById(uid);
        log.info("find: {}", user);
        Assertions.assertNotNull(user);
        Assertions.assertEquals("user", user.getName());
        Assertions.assertEquals(Gender.MAN, user.getGender());
    }

    private void update(Long uid) {
        User user = new User();
        user.setId(uid);
        user.setName("user2");
        Assertions.assertEquals(1, userMapper.updateById(user));
        Assertions.assertEquals("user2", userMapper.selectById(uid).getName());
    }

    private void delete(Long uid) {
        Assertions.assertEquals(1, userMapper.deleteById(uid));
        Assertions.assertNull(userMapper.selectById(uid));
    }

}
