package com.simple.util.asserts;

import com.simple.util.exception.ServiceException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;

/**
 * @author wujing
 * @date 2020/9/7 19:04
 */
public final class ServiceAssert {

    private ServiceAssert() {
    }

    /**
     * 对象是否为空
     *
     * @param obj
     * @param message
     */
    public static void isNull(Object obj, String message) {

        isNull(true, obj, HttpStatus.INTERNAL_SERVER_ERROR, message);
    }

    /**
     * 字符串是否为空
     *
     * @param charSequence
     * @param message
     */
    public static void isBlank(CharSequence charSequence, String message) {

        isBlank(true, charSequence, HttpStatus.INTERNAL_SERVER_ERROR, message);
    }

    /**
     * 集合是否为空
     *
     * @param collection
     * @param message
     */
    public static void isEmpty(Collection<?> collection, String message) {

        isEmpty(true, collection, HttpStatus.INTERNAL_SERVER_ERROR, message);
    }

    /**
     * map是否为空
     *
     * @param map
     * @param message
     */
    public static void isEmpty(Map<?, ?> map, String message) {

        isEmpty(true, map, HttpStatus.INTERNAL_SERVER_ERROR, message);
    }

    /**
     * 对象是否为空
     *
     * @param obj
     * @param status
     * @param message
     */
    public static void isNull(Object obj, HttpStatus status, String message) {

        isNull(true, obj, status, message);
    }

    /**
     * 字符串是否为空
     *
     * @param charSequence
     * @param status
     * @param message
     */
    public static void isBlank(CharSequence charSequence, HttpStatus status, String message) {

        isBlank(true, charSequence, status, message);
    }

    /**
     * 集合是否为空
     *
     * @param collection
     * @param status
     * @param message
     */
    public static void isEmpty(Collection<?> collection, HttpStatus status, String message) {

        isEmpty(true, collection, status, message);
    }

    /**
     * map是否为空
     *
     * @param map
     * @param status
     * @param message
     */
    public static void isEmpty(Map<?, ?> map, HttpStatus status, String message) {

        isEmpty(true, map, status, message);
    }

    /**
     * 对象是否为空
     *
     * @param preCondition
     * @param obj
     * @param status
     * @param message
     */
    public static void isNull(boolean preCondition, Object obj, HttpStatus status, String message) {
        if (preCondition && Objects.isNull(obj)) {

            throw new ServiceException(status, message);
        }
    }

    /**
     * 字符串是否为空
     *
     * @param preCondition
     * @param charSequence
     * @param status
     * @param message
     */
    public static void isBlank(boolean preCondition, CharSequence charSequence, HttpStatus status, String message) {
        if (preCondition && StringUtils.isBlank(charSequence)) {

            throw new ServiceException(status, message);
        }
    }

    /**
     * 集合是否为空
     *
     * @param preCondition
     * @param collection
     * @param status
     * @param message
     */
    public static void isEmpty(boolean preCondition, Collection<?> collection, HttpStatus status, String message) {
        if (preCondition && CollectionUtils.isEmpty(collection)) {

            throw new ServiceException(status, message);
        }
    }

    /**
     * map是否为空
     *
     * @param preCondition
     * @param map
     * @param status
     * @param message
     */
    public static void isEmpty(boolean preCondition, Map<?, ?> map, HttpStatus status, String message) {
        if (preCondition && CollectionUtils.isEmpty(map)) {

            throw new ServiceException(status, message);
        }
    }
}
