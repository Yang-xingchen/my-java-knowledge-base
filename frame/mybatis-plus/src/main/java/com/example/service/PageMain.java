package com.example.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.entry.Gender;
import com.example.entry.User;
import com.example.mapper.UserMapper;
import org.apache.ibatis.executor.BatchResult;
import org.junit.jupiter.api.Assertions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class PageMain implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(PageMain.class);

    @Autowired
    private UserMapper userMapper;

    @Override
    public void run(String... args) throws Exception {
        log.info("==================== {} ====================", getClass().getSimpleName());
        save();
        find();
    }

    private void save() {
        List<User> users = IntStream.range(0, 20).mapToObj(i -> {
            User user = new User();
            user.setName("page-" + i);
            user.setGender(Gender.MAN);
            return user;
        }).toList();
        List<BatchResult> results = userMapper.insert(users);
        Assertions.assertEquals(1, results.size());
        List<Long> uids = users.stream().map(User::getId).toList();
        log.info("save: uid: {}", uids);
        Assertions.assertNotNull(uids);
    }

    private void find() {
        Page<User> page;
        Page<User> users;
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.likeRight(User::getName, "page-");

        page = new Page<>();
        page.setCurrent(1);
        page.setSize(3);
        users = userMapper.selectPage(page, wrapper);
        log.info("find: {}", users);
        Assertions.assertEquals(new HashSet<>(List.of( "page-0", "page-1", "page-2")), users.getRecords().stream().map(User::getName).collect(Collectors.toSet()));

        page = new Page<>();
        page.setCurrent(4);
        page.setSize(6);
        users = userMapper.selectPage(page, wrapper);
        log.info("find: {}", users);
        Assertions.assertEquals(new HashSet<>(List.of( "page-18", "page-19")), users.getRecords().stream().map(User::getName).collect(Collectors.toSet()));
    }

}
