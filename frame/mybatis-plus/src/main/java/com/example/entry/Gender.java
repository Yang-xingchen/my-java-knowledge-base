package com.example.entry;

import com.baomidou.mybatisplus.annotation.IEnum;

public enum Gender implements IEnum<Integer> {

    MAN(0),
    WOMAN(1),
    ;

    public final int value;

    Gender(int value) {
        this.value = value;
    }

    @Override
    public Integer getValue() {
        return value;
    }

}
