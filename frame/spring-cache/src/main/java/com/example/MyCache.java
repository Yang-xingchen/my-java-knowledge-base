package com.example;

import org.springframework.cache.support.AbstractValueAdaptingCache;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

public class MyCache extends AbstractValueAdaptingCache {

    private String name;
    private Map<Object, Object> map = new HashMap<>();

    protected MyCache(String name, boolean allowNullValues) {
        super(allowNullValues);
        this.name = name;
    }

    @Override
    protected Object lookup(Object key) {
        System.out.println("lookup invoke");
        return map.get("key");
    }

    @Override
    public String getName() {
        System.out.println("getName invoke");
        return name;
    }

    @Override
    public Object getNativeCache() {
        System.out.println("getNativeCache invoke");
        return map;
    }

    @Override
    public <T> T get(Object key, Callable<T> valueLoader) {
        System.out.println("get invoke");
        return (T) map.computeIfAbsent(key, k -> {
            try {
                return valueLoader.call();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public void put(Object key, Object value) {
        System.out.println("put invoke");
        map.put(key, value);
    }

    @Override
    public void evict(Object key) {
        System.out.println("evict invoke");
        map.remove(key);
    }

    @Override
    public void clear() {
        System.out.println("clear invoke");
        map.clear();
    }

}
