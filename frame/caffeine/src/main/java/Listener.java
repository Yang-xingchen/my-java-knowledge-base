import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.RemovalCause;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class Listener {

    private Map<String, AtomicInteger> map = new ConcurrentHashMap<>();

    private Integer get(String key) {
        System.out.println("get[" + Thread.currentThread().getName() + "]: " + key);
        return map.computeIfAbsent(key, s -> new AtomicInteger()).incrementAndGet();
    }

    @Test
    public void write() throws Exception {
        Cache<String, Integer> cache = Caffeine.newBuilder()
                .removalListener((String k, Integer v, RemovalCause cause) -> {
                    System.out.println("remove[" + Thread.currentThread().getName() + "]: k: " + k + ", v: " + v + ", c: " + cause);
                })
                .expireAfterWrite(1, TimeUnit.SECONDS)
                .build();

        String key = "key";
        Integer i = cache.get(key, this::get);

        TimeUnit.MILLISECONDS.sleep(600);
        Assertions.assertEquals(i, cache.get(key, this::get));
        TimeUnit.MILLISECONDS.sleep(600);
        Assertions.assertNotEquals(i, cache.get(key, this::get));
    }

}
