package com.simple.util.spring.aop;

/**
 * @author wujing
 * @version 1.3.4
 * @date 2020/10/23 20:50
 */
public class RunState {

    private Object result;
    private int state;

    public RunState(Object result, int state) {
        this.result = result;
        this.state = state;
    }

    public static RunState getInstance(Object result, int state) {

        return new RunState(result, state);
    }

    public Object getResult() {
        return result;
    }

}
