package io.bsoa.rpc.rpc;

import java.io.Serializable;

/**
 * Created by zhanggeng on 2016/12/22.
 */
public class TestObj implements Serializable{

    private static final long serialVersionUID = 3040468179331124640L;
    private String name;

    private int age;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }
}
