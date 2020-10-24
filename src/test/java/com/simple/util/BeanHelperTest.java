package com.simple.util;

import com.simple.util.pojo.Person;
import org.junit.Test;

import java.util.HashMap;

/**
 * @author wujing
 * @date 2020/6/29 19:28
 */
public class BeanHelperTest {


    @Test
    public void mapInToBeanTest() {


        Person person = new Person();
        person.setSex("woman");
        HashMap<String, String> map = new HashMap<>();
        HashMap<String, String> map1 = new HashMap<>();

        map.put("name", "zs");
        map.put("age_1", "19");
        map.put("sex1", "man");

        map1.put("name1", "ls");
        map1.put("Age1", "20");
        map1.put("sex", "man");
        BeanHelper.mapInToBean(map, person).notCaseInsensitive().build();
        BeanHelper.mapInToBean(map1, person).notCaseInsensitive().notReserve().takeOutUnderline().build();


        System.out.println(person);
    }

    @Test
    public void copyPropertiesTest() {

        Person person = new Person();
        person.setSex("man");
        person.setAge1("10");
        person.setName("ls");
        person.setSmart(true);
        Person person1 = BeanHelper.copyProperties(person, Person.class);
        System.out.println(person1);
    }
}