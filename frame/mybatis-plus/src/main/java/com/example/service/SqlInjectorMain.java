package com.example.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.entry.Gender;
import com.example.entry.User;
import com.example.mapper.UserInjectorMapper;
import org.junit.jupiter.api.Assertions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;

import java.util.List;
import java.util.stream.IntStream;

public class SqlInjectorMain implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(SqlInjectorMain.class);

    @Autowired
    private UserInjectorMapper userInjectorMapper;

    @Override
    public void run(String... args) throws Exception {
        log.info("==================== {} ====================", getClass().getSimpleName());
        save();
        find();
    }

    private void save() {
        List<User> users = IntStream.range(0, 20).mapToObj(i -> {
            User user = new User();
            user.setName("injector-" + i);
            user.setGender(Gender.MAN);
            return user;
        }).toList();
        userInjectorMapper.insertBatch(users);
    }

    private void find() {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.likeRight(User::getName, "injector-");
        List<User> users = userInjectorMapper.selectList(wrapper);
        Assertions.assertEquals(20, users.size());
    }


}
