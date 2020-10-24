package com.simple.util.spring.annotation;

import java.lang.annotation.*;

/**
 * Log output of the field toString in the class
 * and the running time of the method through spring aop
 *
 * @author wujing
 * @date 2020/5/14 16:36
 * @see com.simple.util.spring.aop.LogAdvice
 */
@Documented
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface Log {

    /**
     * print field toString for assign annotations
     *
     * @return
     */
    Class<? extends Annotation>[] fieldToString() default {};

    /**
     * whether to open the printing run time-consuming
     *
     * @return
     */
    boolean runExpendTime() default true;
}
