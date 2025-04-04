package com.example;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;

public interface SourceService {

    @Cacheable("source")
    Integer get(String key);

    @CachePut("source")
    Integer update(String key);

    @CacheEvict("source")
    void remove(String key);

}
