package com.simple.util.collection;

import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author wujing
 * @date 2020/4/15 14:59
 */
public class ConcurrentHashSet<E>
        extends AbstractSet<E>
        implements Set<E>, Cloneable, java.io.Serializable {

    private static final long serialVersionUID = -5024744406713321677L;

    private transient ConcurrentHashMap<E, Object> map;

    private static final Object PRESENT = new Object();


    public ConcurrentHashSet() {
        map = new ConcurrentHashMap<>();
    }

    public ConcurrentHashSet(Collection<? extends E> c) {
        map = new ConcurrentHashMap<>(Math.max((int) (c.size() / .75f) + 1, 16));
        addAll(c);
    }


    public ConcurrentHashSet(int initialCapacity, float loadFactor) {
        map = new ConcurrentHashMap<>(initialCapacity, loadFactor);
    }


    public ConcurrentHashSet(int initialCapacity) {
        map = new ConcurrentHashMap<>(initialCapacity);
    }

    @Override
    public Iterator<E> iterator() {
        return map.keySet().iterator();
    }

    @Override
    public int size() {
        return map.size();
    }


    @Override
    public boolean isEmpty() {
        return map.isEmpty();
    }


    @Override
    public boolean contains(Object o) {
        return map.containsKey(o);
    }


    @Override
    public boolean add(E e) {
        return map.put(e, PRESENT) == null;
    }


    @Override
    public boolean remove(Object o) {
        return map.remove(o) == PRESENT;
    }

    @Override
    public void clear() {
        map.clear();
    }

}

