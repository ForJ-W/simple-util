package com.simple.util.spring.aop;

/**
 * @author wujing
 * @date 2020/10/23 20:58
 */
public class LockAdviceTest {

    public static void main(String[] args) {

        Object o = new Object();
        System.out.println(o);

        try {
            o = new Object();
            Object finalO = o;
            new Thread(() -> {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("obj" + finalO);
            }).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
        o = new Object();
    }
}