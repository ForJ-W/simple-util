package com.simple.util.annotation;

import java.lang.annotation.*;

/**
 * @author wujing
 * @date 2020/9/18 16:40
 */
@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ExportIgnore {
}
