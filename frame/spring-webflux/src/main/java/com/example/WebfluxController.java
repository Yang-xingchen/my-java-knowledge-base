package com.example;

import lombok.Data;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.HttpRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

@SuppressWarnings("JavadocLinkAsPlainText")
@RestController
public class WebfluxController {

    /**
     * 无参数
     * http://localhost:8080/base
     */
    @GetMapping("/base")
    public Mono<String> base() {
        return Mono.just("base");
    }

    /**
     * 无注解，使用参数
     * http://localhost:8080/entry?key=k&value=v
     */
    @GetMapping("/entry")
    public Mono<String> entry(Entry e) {
        return Mono.just(e).map(entry -> "entry: " + entry.getKey() + "/" + entry.getValue());
    }

    /**
     * 无注解，使用路径参数
     * http://localhost:8080/entry/k/v
     */
    @GetMapping("/entry/{key}/{value}")
    public Mono<String> entry1(Entry e) {
        return Mono.just(e).map(entry -> "entry: " + entry.getKey() + "/" + entry.getValue());
    }

    @Data
    public static class Entry implements Serializable {
        private String key;
        private String value;
    }

    /**
     * 使用参数
     * http://localhost:8080/param?param=param
     */
    @GetMapping("/param")
    public Mono<String> param(@RequestParam("param") String p) {
        return Mono.just(p).map(param -> "param: " + param);
    }

    /**
     * 可选，使用{@link Optional}代替{@link RequestParam#required()}
     * http://localhost:8080/param1?param1=param1
     * http://localhost:8080/param1
     */
    @GetMapping("/param1")
    public Mono<String> param1(@RequestParam("param1") Optional<String> o) {
        return Mono.just(o).map(optional -> optional.map(param -> "param1: " + param).orElse("param1"));
    }

    /**
     * 路径参数
     * http://localhost:8080/uriPattern/param
     */
    @GetMapping("/uriPattern/{param}")
    public Mono<String> uriPattern(@PathVariable("param") String p) {
        return Mono.just(p).map(param -> "uriPattern: " + param);
    }

    /**
     * 路径参数可用正则
     * http://localhost:8080/uriRegex/param
     */
    @GetMapping("/uriRegex/{param:[a-zA-Z]*}")
    public Mono<String> uriRegex(@PathVariable("param") String p) {
        return Mono.just(p).map(param -> "uriRegex: " + param);
    }

    /**
     * http://localhost:8080/uriRegex/123
     */
    @GetMapping("/uriRegex/{param:[0-9]*}")
    public Mono<String> uriRegex1(@PathVariable("param") Integer p) {
        return Mono.just(p).map(param -> "uriRegex1: " + param);
    }

    /**
     * 矩阵
     * http://localhost:8080/matrix/path;a=a;b=b
     */
    @GetMapping("/matrix/{path}")
    public Mono<String> matrix(@MatrixVariable("a") String a, @MatrixVariable("b") String b) {
        return Mono.just("matrix: a=" + a + ", b=" + b);
    }

    /**
     * http://localhost:8080/matrix1/path;a=a;b=b/path1;a=a1;b=b1
     */
    @GetMapping("/matrix1/{path}/{path1}")
    public Mono<String> matrix1(@MatrixVariable(value = "a", pathVar = "path") String a,
                          @MatrixVariable(value = "a", pathVar = "path1") String a1,
                          @MatrixVariable(value = "b", pathVar = "path") String b,
                          @MatrixVariable(value = "b", pathVar = "path1") String b1) {
        return Mono.just("matrix: a=" + a + ", a1=" + a1 + ", b=" + b + ", b1=" + b1);
    }

    /**
     * http://localhost:8080/matrix2/path;a=a;b=b/path1;a=a1;b=b1
     */
    @GetMapping("/matrix2/{path}/{path1}")
    public Mono<String> matrix2(@MatrixVariable MultiValueMap<String, String> map) {
        // matrix: {a=[a, a1], b=[b, b1]}
        return Mono.just("matrix: " + map);
    }

    /**
     * http头
     * http://localhost:8080/head
     */
    @GetMapping("/head")
    public Mono<String> head(@RequestHeader("myHead") String h) {
        return Mono.just(h).map(host -> "head[myHead]: " + host);
    }

    /**
     * cookie
     * http://localhost:8080/cookie
     */
    @GetMapping("/cookie")
    public Mono<String> cookie(@CookieValue("cookie") String c) {
        return Mono.just(c).map(cookie -> "cookie[cookie]: " + cookie);
    }

    /**
     * {@link ServerHttpRequest}原始请求
     * http://localhost:8080/request
     */
    @GetMapping("/request")
    public Mono<String> request(ServerHttpRequest request) {
        return Mono.just(request).map(HttpRequest::getMethod).map(method -> "request: " + method);
    }

    /**
     * {@link ServerHttpResponse}原始响应
     * http://localhost:8080/response
     */
    @GetMapping("/response")
    public void response(ServerHttpResponse response) throws IOException {
        DefaultDataBufferFactory factory = new DefaultDataBufferFactory();
        DataBuffer dataBuffer = factory.wrap("response".getBytes(StandardCharsets.UTF_8));
        response.writeAndFlushWith(Mono.just(Mono.just(dataBuffer))).subscribe();
    }

    /**
     * 返回json
     * 注意: 需要{@link ResponseBody}或在类上使用{@link RestController}
     * http://localhost:8080/retEntry
     */
    @GetMapping("/retEntry")
    public Mono<Entry> retEntry() {
        return Mono.fromSupplier(() -> {
            Entry entry = new Entry();
            entry.setKey("key");
            entry.setValue("value");
            return entry;
        });
    }

    /**
     * 长连接
     * http://localhost:8080/future
     */
    @GetMapping("/future")
    public Mono<String> future() {
        AtomicLong ret = new AtomicLong();
        CompletableFuture<Long> future;
        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            future = CompletableFuture.supplyAsync(() -> {
                try {
                    TimeUnit.SECONDS.sleep(1);
                    return System.currentTimeMillis() - ret.get();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }, executor);
        }
        ret.set(System.currentTimeMillis());
        return Mono.fromFuture(future).map(Object::toString);
    }

    /**
     * 分多次输出(多次返回)
     * http://localhost:8080/stream
     */
    @GetMapping("/stream")
    public Flux<String> stream() {
        Consumer<FluxSink<String>> consumer = fluxSink -> {
            Thread.ofVirtual().start(() -> {
                try {
                    for (int i = 0; i < 30; i++) {
                        fluxSink.next("send[" + i + "]: " + System.currentTimeMillis() + "\n");
                        TimeUnit.MILLISECONDS.sleep(50);
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                } finally {
                    fluxSink.complete();
                }
            });
        };
        return Flux.create(consumer);
    }

    /**
     * 分多次输出(多次返回)
     * http://localhost:8080/sse
     */
    @GetMapping("/sse")
    public Flux<ServerSentEvent<String>> sse() {
        Consumer<FluxSink<ServerSentEvent<String>>> consumer = fluxSink -> {
            Thread.ofVirtual().start(() -> {
                try {
                    for (int i = 0; i < 30; i++) {
                        ServerSentEvent<String> event = ServerSentEvent.builder("send[" + i + "]: " + System.currentTimeMillis())
                                .build();
                        fluxSink.next(event);
                        TimeUnit.MILLISECONDS.sleep(50);
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                } finally {
                    fluxSink.complete();
                }
            });
        };
        return Flux.create(consumer);
    }

    /**
     *
     */
    @PostMapping("/body")
    public Mono<String> body(@RequestBody String b) {
        return Mono.just(b).map(body -> "body: " + body);
    }

    /**
     * 表单处理
     * http://localhost:8080/form
     */
    @PostMapping("/form")
    public Mono<String> form(@RequestPart("name") String name, @RequestPart("file") FilePart file) {
        return file.content()
                .flatMap(dataBuffer -> Flux.<Byte>push(fluxSink -> {
                    while (dataBuffer.readableByteCount() > 0) {
                        fluxSink.next(dataBuffer.read());
                    }
                    fluxSink.complete();
                }))
                .collectList()
                .map(bytes -> {
                    byte[] bytes1 = new byte[bytes.size()];
                    for (int i = 0; i < bytes.size(); i++) {
                        bytes1[i] = bytes.get(i);
                    }
                    return bytes1;
                })
                .map(String::new)
                .map(content -> "form[" + name + "]: " + content);
    }

    /**
     * 表单处理
     * http://localhost:8080/form1
     */
    @PostMapping("/form1")
    public Mono<String> form1(MyForm f) {
        return Mono.just(f).map(MyForm::getFile)
                .flatMapMany(FilePart::content)
                .flatMap(dataBuffer -> Flux.<Byte>push(fluxSink -> {
                    while (dataBuffer.readableByteCount() > 0) {
                        fluxSink.next(dataBuffer.read());
                    }
                    fluxSink.complete();
                }))
                .collectList()
                .map(bytes -> {
                    byte[] bytes1 = new byte[bytes.size()];
                    for (int i = 0; i < bytes.size(); i++) {
                        bytes1[i] = bytes.get(i);
                    }
                    return bytes1;
                })
                .map(String::new)
                .map(content -> "form1[" + f.name + "]: " + content);
    }

    @Data
    public static class MyForm implements Serializable {
        private String name;
        private FilePart file;
    }

    /**
     * 异常处理{@link #error(MyException)}
     * http://localhost:8080/exception
     */
    @GetMapping("/exception")
    public Mono<String> exception() {
        throw new MyException("ERROR");
    }

    @ExceptionHandler(MyException.class)
    public ResponseEntity<String> error(MyException e) {
        return ResponseEntity.internalServerError().body(e.getMessage());
    }

    public static class MyException extends RuntimeException {
        public MyException(String error) {
            super(error);
        }
    }

}
