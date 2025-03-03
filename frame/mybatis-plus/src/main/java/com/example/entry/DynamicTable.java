package com.example.entry;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;

@TableName("t_dt")
public class DynamicTable implements Serializable {

    @TableId(value = "u_id", type = IdType.AUTO)
    private Long id;
    @TableField("u_name")
    private String name;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "DynamicTable{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }

}
