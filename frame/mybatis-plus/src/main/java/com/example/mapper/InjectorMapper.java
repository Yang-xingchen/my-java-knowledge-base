package com.example.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface InjectorMapper<T> extends BaseMapper<T> {

    int insertBatch(@Param("items") List<T> list);

}
