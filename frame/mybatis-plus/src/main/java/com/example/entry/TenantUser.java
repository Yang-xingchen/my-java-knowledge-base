package com.example.entry;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;

@TableName("t_tenant_user")
public class TenantUser implements Serializable {

    @TableId(value = "u_id", type = IdType.AUTO)
    private Long id;
    @TableField("u_name")
    private String name;
    @TableField("r_id")
    private Long rid;
    @TableField("tenant_id")
    private Long tenant;

    @TableField(exist = false)
    private TenantRole role;

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

    public Long getRid() {
        return rid;
    }

    public void setRid(Long rid) {
        this.rid = rid;
    }

    public Long getTenant() {
        return tenant;
    }

    public void setTenant(Long tenant) {
        this.tenant = tenant;
    }

    public TenantRole getRole() {
        return role;
    }

    public void setRole(TenantRole role) {
        this.role = role;
    }

    @Override
    public String toString() {
        return "User{" +
                "uid=" + id +
                ", name='" + name + '\'' +
                '}';
    }

}
