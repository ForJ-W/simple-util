package com.simple.util.pojo;

/**
 * @author wujing
 * @date 2020/6/29 19:34
 */
public class Person {

    private String name;

    private Boolean smart=false;

    public Boolean getSmart() {
        return smart;
    }

    public void setSmart(Boolean smart) {
        this.smart = smart;
    }

    public String getAge1() {
        return age1;
    }

    public void setAge1(String age1) {
        this.age1 = age1;
    }

    private String age1;
    private String sex;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    @Override
    public String toString() {
        return "Person{" +
                "name='" + name + '\'' +
                ", smart=" + smart +
                ", age1='" + age1 + '\'' +
                ", sex='" + sex + '\'' +
                '}';
    }
}
