package com.example.service;

import com.example.entry.ActiveRecordUser;
import com.example.entry.Gender;
import com.example.entry.User;
import com.example.mapper.UserMapper;
import org.junit.jupiter.api.Assertions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;

public class ActiveRecordMain implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(ActiveRecordMain.class);

    @Override
    public void run(String... args) throws Exception {
        log.info("==================== {} ====================", getClass().getSimpleName());
        Long uid = save();
        find(uid);
        update(uid);
        delete(uid);
    }

    private Long save() {
        ActiveRecordUser user = new ActiveRecordUser();
        user.setName("user");
        user.setGender(Gender.MAN);
        Assertions.assertTrue(user.insert());
        log.info("save: uid: {}", user.getId());
        Assertions.assertNotNull(user.getId());
        return user.getId();
    }

    private void find(Long uid) {
        ActiveRecordUser user = new ActiveRecordUser();
        user.setId(uid);
        user = user.selectById();
        log.info("find: {}", user);
        Assertions.assertNotNull(user);
        Assertions.assertEquals("user", user.getName());
        Assertions.assertEquals(Gender.MAN, user.getGender());
    }

    private void update(Long uid) {
        ActiveRecordUser user = new ActiveRecordUser();
        user.setId(uid);
        user.setName("user2");
        Assertions.assertTrue(user.updateById());
        Assertions.assertEquals("user2", new ActiveRecordUser().selectById(uid).getName());
    }

    private void delete(Long uid) {
        ActiveRecordUser user = new ActiveRecordUser();
        user.setId(uid);
        Assertions.assertTrue(user.deleteById());
        Assertions.assertNull(user.selectById());
    }

}
