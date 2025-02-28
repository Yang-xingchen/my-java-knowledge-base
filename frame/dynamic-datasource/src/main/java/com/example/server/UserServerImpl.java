package com.example.server;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.example.entry.User;
import com.example.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServerImpl implements UserServer{

    @Autowired
    private UserMapper userMapper;

    @DS("master_1")
    @Override
    public Long save1(String name) {
        User user = new User();
        user.setName(name);
        userMapper.save(user);
        return user.getId();
    }

    @DS("master_1")
    @Override
    public String find1(Long id) {
        User user = userMapper.find(id);
        return user != null ? user.getName() : null;
    }

    @DS("master_2")
    @Override
    public Long save2(String name) {
        User user = new User();
        user.setName(name);
        userMapper.save(user);
        return user.getId();
    }

    @DS("master_2")
    @Override
    public String find2(Long id) {
        User user = userMapper.find(id);
        return user != null ? user.getName() : null;
    }

}
