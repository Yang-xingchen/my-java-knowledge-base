import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class InvalidType {

    private Map<String, AtomicInteger> map = new ConcurrentHashMap<>();

    private Integer get(String key) {
        System.out.println("get[" + Thread.currentThread().getName() + "]: " + key);
        return map.computeIfAbsent(key, s -> new AtomicInteger()).incrementAndGet();
    }

    @Test
    public void write() throws Exception {
        Cache<String, Integer> cache = Caffeine.newBuilder()
                // 写后一秒移除
                .expireAfterWrite(1, TimeUnit.SECONDS)
                .build();

        String key = "writeKey";
        Integer i = cache.get(key, this::get);

        TimeUnit.MILLISECONDS.sleep(600);
        Assertions.assertEquals(i, cache.get(key, this::get));
        TimeUnit.MILLISECONDS.sleep(600);
        Assertions.assertNotEquals(i, cache.get(key, this::get));
    }

    @Test
    public void read() throws Exception {
        Cache<String, Integer> cache = Caffeine.newBuilder()
                // 读后一秒移除
                .expireAfterAccess(500, TimeUnit.MILLISECONDS)
                .build();

        String key = "readKey";
        Integer i = cache.get(key, this::get);

        TimeUnit.MILLISECONDS.sleep(300);
        Assertions.assertEquals(i, cache.get(key, this::get));
        TimeUnit.MILLISECONDS.sleep(300);
        Assertions.assertEquals(i, cache.get(key, this::get));
        TimeUnit.MILLISECONDS.sleep(300);
        Assertions.assertEquals(i, cache.get(key, this::get));
        TimeUnit.MILLISECONDS.sleep(300);
        Assertions.assertEquals(i, cache.get(key, this::get));
    }

    @Test
    public void size() throws Exception {
        Cache<String, Integer> cache = Caffeine.newBuilder()
                // 最大数量10个
                .maximumSize(10)
                .build();

        String key = "sizeKey";
        Integer i1 = cache.get(key, this::get);
        for (int i = 0; i < 30; i++) {
            cache.get(key + i, this::get);
        }
        // 并不会立刻移除，等待一段时间
        TimeUnit.MILLISECONDS.sleep(300);
        Assertions.assertNotEquals(i1, cache.get(key, this::get));
    }

    @Test
    public void weight() throws Exception {
        Cache<String, Integer> cache = Caffeine.newBuilder()
                // 可定义不同元素的权重
                .maximumWeight(10)
                .weigher((String k, Integer v) -> v)
                .build();

        String key = "sizeKey";
        Integer i1 = cache.get(key, this::get);
        for (int i = 0; i < 30; i++) {
            cache.get(key + i, this::get);
        }
        TimeUnit.MILLISECONDS.sleep(300);
        Assertions.assertNotEquals(i1, cache.get(key, this::get));
    }

    @Test
    public void ref() throws Exception {
        Cache<String, Integer> cache = Caffeine.newBuilder()
                .weakKeys()
                .weakValues()
                .build();
    }

}
