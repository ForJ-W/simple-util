package com.simple.util.asserts;

import com.simple.util.exception.UtilException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;

/**
 * @author wujing
 * @date 2020/9/7 19:04
 */
public final class UtilAssert {

    private UtilAssert() {
    }

    /**
     * 对象是否为空
     *
     * @param obj
     * @param message
     */
    public static void isNull(Object obj, String message) {

        isNull(true, obj, message);
    }

    /**
     * @param charSequence
     * @param message
     */
    public static void isBlank(CharSequence charSequence, String message) {

        isBlank(true, charSequence, message);
    }

    /**
     * @param collection
     * @param message
     */
    public static void isEmpty(Collection<?> collection, String message) {

        isEmpty(true, collection, message);
    }

    /**
     * @param map
     * @param message
     */
    public static void isEmpty(Map<?, ?> map, String message) {

        isEmpty(true, map, message);
    }

    /**
     * @param preCondition
     * @param obj
     * @param message
     */
    public static void isNull(boolean preCondition, Object obj, String message) {
        if (preCondition && Objects.isNull(obj)) {

            throw new UtilException(message);
        }
    }

    /**
     * @param preCondition
     * @param charSequence
     * @param message
     */
    public static void isBlank(boolean preCondition, CharSequence charSequence, String message) {
        if (preCondition && StringUtils.isBlank(charSequence)) {

            throw new UtilException(message);
        }
    }

    /**
     * @param preCondition
     * @param collection
     * @param message
     */
    public static void isEmpty(boolean preCondition, Collection<?> collection, String message) {
        if (preCondition && CollectionUtils.isEmpty(collection)) {

            throw new UtilException(message);
        }
    }

    /**
     * @param preCondition
     * @param map
     * @param message
     */
    public static void isEmpty(boolean preCondition, Map<?, ?> map, String message) {
        if (preCondition && CollectionUtils.isEmpty(map)) {

            throw new UtilException(message);
        }
    }
}
