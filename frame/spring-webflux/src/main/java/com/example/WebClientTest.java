package com.example;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class WebClientTest {

    private final WebClient restClient = WebClient.builder()
            .baseUrl("http://localhost:8080")
            .build();

    @Test
    public void base() {
        String res = restClient.get()
                .uri("/base")
                .retrieve()
                .bodyToMono(String.class)
                .block();
        Assertions.assertEquals("base", res);
    }

    @Test
    public void entry() {
        String res = restClient.get()
                .uri("/entry?key=k&value=v")
                .retrieve()
                .bodyToMono(String.class)
                .block();
        Assertions.assertEquals("entry: k/v", res);
    }

    @Test
    public void entry1() {
        String res = restClient.get()
                .uri("/entry/k/v")
                .retrieve()
                .bodyToMono(String.class)
                .block();
        Assertions.assertEquals("entry: k/v", res);
    }

    @Test
    public void param() {
        String res = restClient.get()
                .uri("/param?param=param")
                .retrieve()
                .bodyToMono(String.class)
                .block();
        Assertions.assertEquals("param: param", res);
    }

    @Test
    public void param1() {
        String res = restClient.get()
                .uri("/param1?param1=param1")
                .retrieve()
                .bodyToMono(String.class)
                .block();
        Assertions.assertEquals("param1: param1", res);
        String res1 = restClient.get()
                .uri("/param1")
                .retrieve()
                .bodyToMono(String.class)
                .block();
        Assertions.assertEquals("param1", res1);
    }

    @Test
    public void uriPattern() {
        String res = restClient.get()
                .uri("/uriPattern/param")
                .retrieve()
                .bodyToMono(String.class)
                .block();
        Assertions.assertEquals("uriPattern: param", res);
    }

    @Test
    public void uriRegex() {
        String res = restClient.get()
                .uri("/uriRegex/param")
                .retrieve()
                .bodyToMono(String.class)
                .block();
        Assertions.assertEquals("uriRegex: param", res);
    }

    @Test
    public void uriRegex1() {
        String res = restClient.get()
                .uri("/uriRegex/123")
                .retrieve()
                .bodyToMono(String.class)
                .block();
        Assertions.assertEquals("uriRegex1: 123", res);
    }

    @Test
    public void matrix() {
        String res = restClient.get()
                .uri("/matrix/path;a=a;b=b")
                .retrieve()
                .bodyToMono(String.class)
                .block();
        Assertions.assertEquals("matrix: a=a, b=b", res);
    }

    @Test
    public void matrix1() {
        String res = restClient.get()
                .uri("/matrix1/path;a=a;b=b/path1;a=a1;b=b1")
                .retrieve()
                .bodyToMono(String.class)
                .block();
        Assertions.assertEquals("matrix: a=a, a1=a1, b=b, b1=b1", res);
    }

    @Test
    public void matrix2() {
        String res = restClient.get()
                .uri("/matrix2/path;a=a;b=b/path1;a=a1;b=b1")
                .retrieve()
                .bodyToMono(String.class)
                .block();
        Assertions.assertEquals("matrix: {a=[a, a1], b=[b, b1]}", res);
    }

    @Test
    public void head() {
        String res = restClient.get()
                .uri("/head")
                .header("myHead", "head")
                .retrieve()
                .bodyToMono(String.class)
                .block();
        Assertions.assertEquals("head[myHead]: head", res);
    }

    @Test
    public void cookie() {
        String res = restClient.get()
                .uri("/cookie")
                .headers(httpHeaders -> httpHeaders.add(HttpHeaders.COOKIE, "cookie=cookie"))
                .retrieve()
                .bodyToMono(String.class)
                .block();
        Assertions.assertEquals("cookie[cookie]: cookie", res);
    }

    @Test
    public void request() {
        String res = restClient.get()
                .uri("/request")
                .retrieve()
                .bodyToMono(String.class)
                .block();
        Assertions.assertEquals("request: GET", res);
    }

    @Test
    public void response()  {
        String res = restClient.get()
                .uri("/response")
                .retrieve()
                .bodyToMono(String.class)
                .block();
        Assertions.assertEquals("response", res);
    }

    @Test
    public void retEntry() {
        String res = restClient.get()
                .uri("/retEntry")
                .retrieve()
                .bodyToMono(String.class)
                .block();
        Assertions.assertEquals("{\"key\":\"key\",\"value\":\"value\"}", res);
    }

    @Test
    public void future() {
        String res = restClient.get()
                .uri("/future")
                .retrieve()
                .bodyToMono(String.class)
                .block();
        Assertions.assertTrue(Long.parseLong(res) >= 1000);
    }

    @Test
    public void stream() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        restClient.get()
                .uri("/stream")
                .retrieve()
                .bodyToFlux(String.class)
                .map(s -> System.currentTimeMillis() + ": " + s)
                .doOnComplete(latch::countDown)
                .subscribe(System.out::println);
        latch.await();
    }

    @Test
    public void sse() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        restClient.get()
                .uri("/sse")
                .retrieve()
                .bodyToFlux(String.class)
                .map(s -> System.currentTimeMillis() + ": " + s)
                .doOnComplete(latch::countDown)
                .subscribe(System.out::println);
        latch.await();
    }

    @Test
    public void body() {
        String res = restClient.post()
                .uri("/body")
                .bodyValue("TEST")
                .retrieve()
                .bodyToMono(String.class)
                .block();
        Assertions.assertEquals("body: TEST", res);
    }

    @Test
    public void form() throws IOException {
        Path path = Files.writeString(Paths.get("form.txt"), "TEST");
        String res;
        try {
            MultipartBodyBuilder builder = new MultipartBodyBuilder();
            builder.part("name", "test");
            builder.part("file", new FileSystemResource(path));
            res = restClient.post()
                    .uri("/form")
                    .bodyValue(builder.build())
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
        } finally {
            Files.delete(path);
        }
        Assertions.assertEquals("form[test]: TEST", res);
    }

    @Test
    public void form1() throws IOException {
        Path path = Files.writeString(Paths.get("form1.txt"), "TEST");
        String res;
        try {
            MultipartBodyBuilder builder = new MultipartBodyBuilder();
            builder.part("name", "test");
            builder.part("file", new FileSystemResource(path));
            res = restClient.post()
                    .uri("/form1")
                    .bodyValue(builder.build())
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
        } finally {
            Files.delete(path);
        }
        Assertions.assertEquals("form1[test]: TEST", res);
    }

    @Test
    public void exception() {
        String res = restClient.get()
                .uri("/exception")
                .retrieve()
                .onStatus(HttpStatusCode::is5xxServerError, response -> response.bodyToMono(String.class).map(RuntimeException::new))
                .bodyToMono(String.class)
                .onErrorResume(throwable -> Mono.just(throwable.getMessage()))
                .block();
        Assertions.assertEquals("ERROR", res);
    }

}
