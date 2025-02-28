package com.example.mapper;

import com.example.entry.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

@Mapper
@Component
public interface UserMapper {

    int save(@Param("user") User user);

    User find(@Param("id") Long uid);

}
