package com.example;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class SourceServiceImpl implements SourceService{

    private Map<String, AtomicInteger> map = new ConcurrentHashMap<>();

    @Override
    public Integer get(String key) {
        System.out.println("get[" + Thread.currentThread().getName() + "]: " + key);
        return map.computeIfAbsent(key, s -> new AtomicInteger()).incrementAndGet();
    }

    @Override
    public Integer update(String key) {
        System.out.println("update[" + Thread.currentThread().getName() + "]: " + key);
        return map.computeIfAbsent(key, s -> new AtomicInteger()).incrementAndGet();
    }

    @Override
    public void remove(String key) {
        System.out.println("remove[" + Thread.currentThread().getName() + "]: " + key);
    }

}
