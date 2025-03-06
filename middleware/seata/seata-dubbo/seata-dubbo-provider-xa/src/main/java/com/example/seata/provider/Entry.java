package com.example.seata.provider;

import java.io.Serializable;

public class Entry implements Serializable {

    private Long id;
    private Integer val;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getVal() {
        return val;
    }

    public void setVal(Integer val) {
        this.val = val;
    }
}
