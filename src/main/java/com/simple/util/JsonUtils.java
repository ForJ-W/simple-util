package com.simple.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.simple.util.poi.POIHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author wujing
 * @date 2020/3/25 15:36
 */
public final class JsonUtils {

    public static final ObjectMapper MAPPER = new ObjectMapper();
    private static final Logger logger = LoggerFactory.getLogger(POIHelper.class);


    private JsonUtils() {
    }

    /**
     * obj to string
     * @param obj
     * @return
     */
    public static String toString(Object obj) {

        if (obj == null) {

            return null;
        }
        if (obj.getClass() == String.class) {

            return (String) obj;
        }
        try {

            return MAPPER.writeValueAsString(obj);
        } catch (JsonProcessingException e) {

            logger.error("json serialization error:" + obj, e);
            return null;
        }
    }


    /**
     * json to obj
     * @param json
     * @param tClass
     * @param <T>
     * @return
     */
    public static <T> T toBean(String json, Class<T> tClass) {
        try {
            return MAPPER.readValue(json, tClass);
        } catch (IOException e) {
            logger.error("json parse error:" + json, e);
            return null;
        }
    }

    /**
     * json to list
     * @param json
     * @param eClass
     * @param <E>
     * @return
     */
    public static <E> List<E> toList(String json, Class<E> eClass) {
        try {
            return MAPPER.readValue(json, MAPPER.getTypeFactory().constructCollectionType(List.class, eClass));
        } catch (IOException e) {
            logger.error("json parse error:" + json, e);
            return null;
        }
    }

    /**
     * json to Set
     * @param json
     * @param eClass
     * @param <E>
     * @return
     */
    public static <E> Set<E> toSet(String json, Class<E> eClass) {
        try {
            return MAPPER.readValue(json, MAPPER.getTypeFactory().constructCollectionType(Set.class, eClass));
        } catch (IOException e) {
            logger.error("json parse error:" + json, e);
            return null;
        }
    }

    /**
     * json to map
     * @param json
     * @param kClass
     * @param vClass
     * @param <K>
     * @param <V>
     * @return
     */
    public static <K, V> Map<K, V> toMap(String json, Class<K> kClass, Class<V> vClass) {
        try {
            return MAPPER.readValue(json, MAPPER.getTypeFactory().constructMapType(LinkedHashMap.class, kClass, vClass));
        } catch (IOException e) {
            logger.error("json parse error:" + json, e);
            return null;
        }
    }

    /**
     * 自定义json to type
     * @param json
     * @param type
     * @param <T>
     * @return
     */
    public static <T> T nativeRead(String json, TypeReference<T> type) {
        try {
            return MAPPER.readValue(json, type);
        } catch (IOException e) {
            logger.error("json parse error:" + json, e);
            return null;
        }
    }
}
