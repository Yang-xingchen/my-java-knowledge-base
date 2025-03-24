package com.example;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class RestClientTest {

    private final RestClient restClient = RestClient.builder()
            .baseUrl("http://localhost:8080")
            .build();

    @Test
    public void base() {
        String res = restClient.get().uri("/base").retrieve().body(String.class);
        Assertions.assertEquals("base", res);
    }

    @Test
    public void entry() {
        String res = restClient.get()
                .uri("/entry?key=k&value=v")
                .retrieve()
                .body(String.class);
        Assertions.assertEquals("entry: k/v", res);
    }

    @Test
    public void entry1() {
        String res = restClient.get()
                .uri("/entry/k/v")
                .retrieve()
                .body(String.class);
        Assertions.assertEquals("entry: k/v", res);
    }

    @Test
    public void param() {
        String res = restClient.get()
                .uri("/param?param=param")
                .retrieve()
                .body(String.class);
        Assertions.assertEquals("param: param", res);
    }

    @Test
    public void param1() {
        String res = restClient.get()
                .uri("/param1?param1=param1")
                .retrieve()
                .body(String.class);
        Assertions.assertEquals("param1: param1", res);
        String res1 = restClient.get()
                .uri("/param1")
                .retrieve()
                .body(String.class);
        Assertions.assertEquals("param1", res1);
    }

    @Test
    public void uriPattern() {
        String res = restClient.get()
                .uri("/uriPattern/param")
                .retrieve()
                .body(String.class);
        Assertions.assertEquals("uriPattern: param", res);
    }

    @Test
    public void uriRegex() {
        String res = restClient.get()
                .uri("/uriRegex/param")
                .retrieve()
                .body(String.class);
        Assertions.assertEquals("uriRegex: param", res);
    }

    @Test
    public void uriRegex1() {
        String res = restClient.get()
                .uri("/uriRegex/123")
                .retrieve()
                .body(String.class);
        Assertions.assertEquals("uriRegex1: 123", res);
    }

    @Test
    public void matrix() {
        String res = restClient.get()
                .uri("/matrix/path;a=a;b=b")
                .retrieve()
                .body(String.class);
        Assertions.assertEquals("matrix: a=a, b=b", res);
    }

    @Test
    public void matrix1() {
        String res = restClient.get()
                .uri("/matrix1/path;a=a;b=b/path1;a=a1;b=b1")
                .retrieve()
                .body(String.class);
        Assertions.assertEquals("matrix: a=a, a1=a1, b=b, b1=b1", res);
    }

    @Test
    public void matrix2() {
        String res = restClient.get()
                .uri("/matrix2/path;a=a;b=b/path1;a=a1;b=b1")
                .retrieve()
                .body(String.class);
        Assertions.assertEquals("matrix: {a=[a, a1], b=[b, b1]}", res);
    }

    @Test
    public void head() {
        String res = restClient.get()
                .uri("/head")
                .header("myHead", "head")
                .retrieve()
                .body(String.class);
        Assertions.assertEquals("head[myHead]: head", res);
    }

    @Test
    public void cookie() {
        String res = restClient.get()
                .uri("/cookie")
                .headers(httpHeaders -> httpHeaders.add(HttpHeaders.COOKIE, "cookie=cookie"))
                .retrieve()
                .body(String.class);
        Assertions.assertEquals("cookie[cookie]: cookie", res);
    }

    @Test
    public void request() {
        String res = restClient.get()
                .uri("/request")
                .retrieve()
                .body(String.class);
        Assertions.assertEquals("request: GET", res);
    }

    @Test
    public void response()  {
        String res = restClient.get()
                .uri("/response")
                .retrieve()
                .body(String.class);
        Assertions.assertEquals("response", res);
    }

    @Test
    public void retEntry() {
        String res = restClient.get()
                .uri("/retEntry")
                .retrieve()
                .body(String.class);
        Assertions.assertEquals("{\"key\":\"key\",\"value\":\"value\"}", res);
    }

    @Test
    public void deferred() {
        String res = restClient.get()
                .uri("/deferred")
                .retrieve()
                .body(String.class);
        Assertions.assertTrue(Long.parseLong(res) >= 1000);
    }

    @Test
    public void stream() {
        String res = restClient.get()
                .uri("/stream")
                .retrieve()
                .body(String.class);
        System.out.println(res);
    }

    @Test
    public void body() {
        String res = restClient.post()
                .uri("/body")
                .body("TEST")
                .retrieve()
                .body(String.class);
        Assertions.assertEquals("body: TEST", res);
    }

    @Test
    public void sse() {
        // 该测试不明显，建议使用浏览器访问
        String res = restClient.get()
                .uri("/sse")
                .retrieve()
                .body(String.class);
        System.out.println(res);
    }

    @Test
    public void form() throws IOException {
        Path path = Files.writeString(Paths.get("form.txt"), "TEST");
        String res;
        try {
            MultiValueMap<String, Object> form = new LinkedMultiValueMap<>();
            form.add("name", "test");
            form.add("file", new FileSystemResource(path));
            res = restClient.post()
                    .uri("/form")
                    .body(form)
                    .retrieve()
                    .body(String.class);
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
            MultiValueMap<String, Object> form = new LinkedMultiValueMap<>();
            form.add("name", "test");
            form.add("file", new FileSystemResource(path));
            res = restClient.post()
                    .uri("/form1")
                    .body(form)
                    .retrieve()
                    .body(String.class);
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
                .onStatus(HttpStatusCode::is5xxServerError, (request, response) -> {})
                .body(String.class);
        Assertions.assertEquals("ERROR", res);
    }

}
