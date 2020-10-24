package com.simple.util.collection;

import org.apache.commons.lang3.StringUtils;

import java.util.*;

/**
 * @author wujing
 * @date 2020/4/22 15:31
 */
@SuppressWarnings("unchecked")
public final class CollectionHelper {

    private CollectionHelper() {
    }

    /**
     * string to set
     *
     * @param value
     * @return
     */
    public static Set<String> convertToSet(String value, String regex) {
        if (StringUtils.isBlank(value)) {
            return null;
        }

        Set<String> cell = new LinkedHashSet<>();
        if (!value.contains(regex)) {

            cell.add(value);
            return cell;
        }

        String[] split = value.split(regex);
        Collections.addAll(cell, split);
        return cell;
    }

    /**
     * 判断并设置map中的集合
     *
     * @param targetMap
     * @param key
     * @param targetValue
     * @param <T>
     */
    public static <K, T> void judgeSetMap(Map<K, Collection<T>> targetMap, K key, T... targetValue) {

        boolean flag = targetMap.containsKey(key);
        Collection<T> targetCollection;
        if (flag) {
            targetCollection = targetMap.get(key);
        } else {
            targetCollection = new LinkedHashSet<>(4);
        }
        if (targetValue != null) {
            for (int i = 0; i < targetValue.length; i++) {

                T t = targetValue[i];
                if (null != t) {
                    targetCollection.add(t);
                }
            }
        }

        targetMap.put(key, targetCollection);
    }

    /**
     * 判断数量是否一致
     *
     * @param oldCount 旧数量
     * @param newCount 新数量
     * @return
     */
    public static boolean judgeCount(int oldCount, int newCount) {
        if (oldCount > 0 && newCount > 0 && oldCount == newCount) {

            return true;
        }
        return false;
    }
}
