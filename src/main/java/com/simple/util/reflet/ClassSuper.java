package com.simple.util.reflet;

import org.apache.commons.lang3.ArrayUtils;

import java.lang.reflect.Field;
import java.util.*;

/**
 * @author wujing
 * @date 2020/10/21 15:32
 */
public final class ClassSuper<T> implements Iterable<Class<? super T>>, Iterator<Class<? super T>> {

    private Class<? super T> cls;
    private final Class<? super T> base;
    private final LinkedList<Class<? super T>> extentsClassList = new LinkedList<>();
    private final LinkedList<Class<? super T>> extentsClassReverseList = new LinkedList<>();

    @Override
    public Iterator<Class<? super T>> iterator() {
        return this;
    }

    @Override
    public boolean hasNext() {

        return cls != Object.class;
    }

    @Override
    public Class<? super T> next() {
        Class<? super T> next = cls;
        cls = cls.getSuperclass();
        return next;
    }

    /**
     * getInstance
     *
     * @param cls
     * @param <E>
     * @return
     */
    public static <E> ClassSuper<E> getInstance(Class<E> cls) {

        return new ClassSuper<>(cls);
    }

    private ClassSuper(Class<T> cls) {
        this.cls = cls;
        this.base = cls;
    }

    /**
     * 获取继承体系的所有class 顶层至自身
     *
     * @return
     */
    public List<Class<? super T>> getExtentsReverseList() {

        cls = base;
        if (0 == extentsClassReverseList.size()) {

            for (Class<? super T> c : this) {
                extentsClassReverseList.addFirst(c);
            }
        }
        return extentsClassReverseList;
    }

    /**
     * 获取继承体系的所有class 自身至顶层
     *
     * @return
     */
    public List<Class<? super T>> getExtentsList() {

        cls = base;
        if (0 == extentsClassList.size()) {

            for (Class<? super T> c : this) {
                extentsClassList.addLast(c);
            }
        }
        return extentsClassList;
    }

    /**
     * 获取继承体系的所有属性 顶层至自身
     *
     * @return
     */
    public Field[] getExtentsReverseFields() {

        List<Class<? super T>> extentsList = getExtentsReverseList();
        Field[] extentsFieldArr = null;
        for (Class<? super T> cls : extentsList) {

            extentsFieldArr = ArrayUtils.addAll(extentsFieldArr, cls.getDeclaredFields());
        }
        return extentsFieldArr;
    }

    /**
     * 获取继承体系的所有属性 自身至顶层
     *
     * @return
     */
    public Field[] getExtentsFields() {

        List<Class<? super T>> extentsList = getExtentsList();
        Field[] extentsFieldArr = null;
        for (Class<? super T> cls : extentsList) {

            extentsFieldArr = ArrayUtils.addAll(extentsFieldArr, cls.getDeclaredFields());
        }
        return extentsFieldArr;
    }
}