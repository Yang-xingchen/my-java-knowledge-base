package com.example.mapper;

import com.example.entry.User;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

@Mapper
@Component
public interface UserInjectorMapper extends InjectorMapper<User> {
}
