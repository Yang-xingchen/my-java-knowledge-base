import com.github.benmanes.caffeine.cache.*;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class WriteType {

    private final Map<String, AtomicInteger> map = new ConcurrentHashMap<>();
    private final Executor executor = new ThreadPoolExecutor(
            1, 1,
            1, TimeUnit.MINUTES,
            new ArrayBlockingQueue<>(16),
            r -> {
                Thread thread = new Thread(r);
                thread.setName("executor");
                return thread;
            }
    );

    private Integer get(String key) {
        System.out.println("get[" + Thread.currentThread().getName() + "]: " + key);
        return map.computeIfAbsent(key, s -> new AtomicInteger()).incrementAndGet();
    }

    @Test
    public void base() throws InterruptedException {
        // 普通同步cache，get中方法获取源数据
        Cache<String, Integer> cache = Caffeine.newBuilder()
                .build();

        String key = "key";
        Integer i = cache.get(key, this::get);
    }

    @Test
    public void loading() throws InterruptedException {
        // 加载同步cache，build方法配置获取源数据
        LoadingCache<String, Integer> cache = Caffeine.newBuilder()
                .build(this::get);

        String key = "loadingKey";
        Integer i = cache.get(key);
    }

    @Test
    public void baseAsync() throws Exception {
        // 普通异步cache，get中方法获取源数据
        AsyncCache<String, Integer> cache = Caffeine.newBuilder()
                .executor(executor)
                .buildAsync();

        String key = "asyncKey";
        CompletableFuture<Integer> future = cache.get(key, this::get);
        Integer i = future.get();
    }

    @Test
    public void loadingAsync() throws Exception {
        // 普通异步cache，get中方法获取源数据
        AsyncLoadingCache<String, Integer> cache = Caffeine.newBuilder()
                .executor(executor)
                .buildAsync(this::get);

        String key = "loadingAsyncKey";
        CompletableFuture<Integer> future = cache.get(key);
        Integer i = future.get();
    }

}
