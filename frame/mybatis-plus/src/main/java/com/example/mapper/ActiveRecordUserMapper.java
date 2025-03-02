package com.example.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.entry.ActiveRecordUser;
import com.example.entry.User;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

@Mapper
@Component
public interface ActiveRecordUserMapper extends BaseMapper<ActiveRecordUser> {
}
