package com.example.entry;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;

@TableName("t_lock_user")
public class LockUser implements Serializable {

    @TableId(value = "u_id", type = IdType.AUTO)
    private Long id;
    @TableField("u_name")
    private String name;
    @Version
    @TableField("v_lock")
    private Long version;

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

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    @Override
    public String toString() {
        return "LockUser{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", version=" + version +
                '}';
    }

}
