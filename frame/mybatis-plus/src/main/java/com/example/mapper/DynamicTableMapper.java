package com.example.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.entry.DynamicTable;
import com.example.entry.TenantUser;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

@Mapper
@Component
public interface DynamicTableMapper extends BaseMapper<DynamicTable> {
}
