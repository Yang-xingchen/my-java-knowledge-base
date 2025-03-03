package com.example.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.example.entry.Gender;
import com.example.entry.User;
import com.example.mapper.UserMapper;
import org.junit.jupiter.api.Assertions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;

public class LambdaWrapperMain implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(LambdaWrapperMain.class);

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
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getId, uid).last("limit 1");
        User user = userMapper.selectOne(wrapper);
        log.info("find: {}", user);
        Assertions.assertNotNull(user);
        Assertions.assertEquals("user", user.getName());
        Assertions.assertEquals(Gender.MAN, user.getGender());
    }

    private void update(Long uid) {
        LambdaUpdateWrapper<User> wrapper = new LambdaUpdateWrapper<>();
        wrapper.set(User::getName, "user2").eq(User::getId, uid);
        Assertions.assertEquals(1, userMapper.update(wrapper));
        Assertions.assertEquals("user2", userMapper.selectById(uid).getName());
    }

    private void delete(Long uid) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getId, uid).last("limit 1");
        Assertions.assertEquals(1, userMapper.delete(wrapper));
        Assertions.assertNull(userMapper.selectById(uid));
    }

}
