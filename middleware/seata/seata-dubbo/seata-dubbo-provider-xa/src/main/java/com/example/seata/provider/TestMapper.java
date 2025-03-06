package com.example.seata.provider;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

@Mapper
@Component
public interface TestMapper {

    void save(@Param("param") Entry entry);

    void add(@Param("id") Long id, @Param("val") Integer val);

    Integer get(@Param("id") Long id);

    void delete(@Param("id") Long id);

}
