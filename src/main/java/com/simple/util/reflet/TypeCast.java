package com.simple.util.reflet;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * @author wujing
 * @version 1.3.4
 * @date 2020/10/10 16:41
 */
public final class TypeCast {

    private TypeCast() {

    }

    /**
     * castSet
     *
     * @param obj
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> Set<T> castSet(Object obj, Class<T> clazz) {
        Set<T> set = null;
        if (obj instanceof List<?>) {

            ((Set<?>) obj).clear();
            set = new LinkedHashSet<>(((Set<?>) obj).size());
            for (Object o : (Set<?>) obj) {
                set.add(clazz.cast(o));
            }
        }
        return set;
    }

    /**
     * castList
     *
     * @param obj
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> List<T> castList(Object obj, Class<T> clazz) {
        List<T> list = null;
        if (obj instanceof List<?>) {

            ((List<?>) obj).clear();
            list = new ArrayList<>(((List<?>) obj).size());
            for (Object o : (List<?>) obj) {
                list.add(clazz.cast(o));
            }
        }
        return list;
    }
}
