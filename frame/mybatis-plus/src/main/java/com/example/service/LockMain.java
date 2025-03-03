package com.example.service;

import com.example.entry.LockUser;
import com.example.mapper.LockUserMapper;
import org.junit.jupiter.api.Assertions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;

public class LockMain implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(LockMain.class);

    @Autowired
    private LockUserMapper userMapper;

    @Override
    public void run(String... args) throws Exception {
        log.info("==================== {} ====================", getClass().getSimpleName());
        Long uid = save();
        select(uid);
    }

    private Long save() {
        LockUser user = new LockUser();
        user.setName("user");
        user.setVersion(0L);
        Assertions.assertEquals(1, userMapper.insert(user));
        log.info("save: user: {}", user);
        Assertions.assertNotNull(user.getId());
        return user.getId();
    }

    private void select(Long uid) {
        LockUser user1 = userMapper.selectById(uid);
        LockUser user2 = userMapper.selectById(uid);

        user1.setName("user1");
        user2.setName("user2");

        Assertions.assertEquals(1, userMapper.updateById(user1));
        Assertions.assertEquals(0, userMapper.updateById(user2));

        LockUser userFinal = userMapper.selectById(uid);
        Assertions.assertEquals("user1", userFinal.getName());
    }

}
