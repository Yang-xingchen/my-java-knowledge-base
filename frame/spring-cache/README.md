# spring cache
缓存，基于AOP实现。

[文档(spring framework)](https://docs.spring.io/spring-framework/reference/integration/cache.html)
[文档(spring boot)](https://docs.spring.io/spring-boot/reference/io/caching.html)

# 注解
- `@EnableCaching`: 配置类使用，启用缓存
- `@Cacheable`: 缓存结果，如果缓存内容存在，则直接使用缓存不调用源方法
- `@CachePut`: 更新缓存，调用源方法并将结果保存/更新缓存
- `@CacheEvict`: 移除缓存
- `@Caching`: 缓存操作组合
- `@CacheConfig`: 类级别的配置

# 缓存类型
- `simple`: (默认)基于`java.util.concurrent.ConcurrentHashMap`
- `redis`: 基于[redis](../../middleware/redis)，需添加redis相关依赖
- `caffeine`: 基于[caffeine](../caffeine)，需添加caffeine相关依赖
- `generic`: 自定义缓存，需手动添加bean，[MyCache.java](src/main/java/com/example/MyCache.java)
- ...