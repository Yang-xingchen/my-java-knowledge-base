package com.example;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class RestTemplateTest {

    private final RestTemplate restTemplate = new RestTemplateBuilder()
            .rootUri("http://localhost:8080")
            .build();

    @Test
    public void base() {
        String res = restTemplate.getForObject("/base", String.class);
        Assertions.assertEquals("base", res);
    }

    @Test
    public void entry() {
        String res = restTemplate.getForObject("/entry?key=k&value=v", String.class);
        Assertions.assertEquals("entry: k/v", res);
    }

    @Test
    public void entry1() {
        String res = restTemplate.getForObject("/entry/k/v", String.class);
        Assertions.assertEquals("entry: k/v", res);
    }

    @Test
    public void param() {
        String res = restTemplate.getForObject("/param?param=param", String.class);
        Assertions.assertEquals("param: param", res);
    }

    @Test
    public void param1() {
        String res = restTemplate.getForObject("/param1?param1=param1", String.class);
        Assertions.assertEquals("param1: param1", res);
        String res1 = restTemplate.getForObject("/param1", String.class);
        Assertions.assertEquals("param1", res1);
    }

    @Test
    public void uriPattern() {
        String res = restTemplate.getForObject("/uriPattern/param", String.class);
        Assertions.assertEquals("uriPattern: param", res);
    }

    @Test
    public void uriRegex() {
        String res = restTemplate.getForObject("/uriRegex/param", String.class);
        Assertions.assertEquals("uriRegex: param", res);
    }

    @Test
    public void uriRegex1() {
        String res = restTemplate.getForObject("/uriRegex/123", String.class);
        Assertions.assertEquals("uriRegex1: 123", res);
    }

    @Test
    public void matrix() {
        String res = restTemplate.getForObject("/matrix/path;a=a;b=b", String.class);
        Assertions.assertEquals("matrix: a=a, b=b", res);
    }

    @Test
    public void matrix1() {
        String res = restTemplate.getForObject("/matrix1/path;a=a;b=b/path1;a=a1;b=b1", String.class);
        Assertions.assertEquals("matrix: a=a, a1=a1, b=b, b1=b1", res);
    }

    @Test
    public void matrix2() {
        String res = restTemplate.getForObject("/matrix2/path;a=a;b=b/path1;a=a1;b=b1", String.class);
        Assertions.assertEquals("matrix: {a=[a, a1], b=[b, b1]}", res);
    }

    @Test
    public void head() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("myHead", "head");
        HttpEntity<Void> entity = new HttpEntity<>(null, headers);
        ResponseEntity<String> responseEntity = restTemplate.exchange("/head", HttpMethod.GET, entity, String.class);
        Assertions.assertEquals("head[myHead]: head", responseEntity.getBody());
    }

    @Test
    public void cookie() {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.COOKIE, "cookie=cookie");
        HttpEntity<Void> entity = new HttpEntity<>(null, headers);
        ResponseEntity<String> responseEntity = restTemplate.exchange("/cookie", HttpMethod.GET, entity, String.class);
        Assertions.assertEquals("cookie[cookie]: cookie", responseEntity.getBody());
    }

    @Test
    public void request() {
        String res = restTemplate.getForObject("/request", String.class);
        Assertions.assertEquals("request: GET", res);
    }

    @Test
    public void response()  {
        String res = restTemplate.getForObject("/response", String.class);
        Assertions.assertEquals("response", res);
    }

    @Test
    public void retEntry() {
        String res = restTemplate.getForObject("/retEntry", String.class);
        Assertions.assertEquals("{\"key\":\"key\",\"value\":\"value\"}", res);
    }

    @Test
    public void deferred() {
        String res = restTemplate.getForObject("/deferred", String.class);
        Assertions.assertTrue(Long.parseLong(res) >= 1000);
    }

    @Test
    public void stream() {
        String res = restTemplate.getForObject("/stream", String.class);
        System.out.println(res);
    }

    @Test
    public void sse() {
        // 该测试不明显，建议使用浏览器访问
        String res = restTemplate.getForObject("/sse", String.class);
        System.out.println(res);
    }

    @Test
    public void body() {
        String res = restTemplate.postForObject("/body", "TEST", String.class);
        Assertions.assertEquals("body: TEST", res);
    }

    @Test
    public void form() throws IOException {
        Path path = Files.writeString(Paths.get("form.txt"), "TEST");
        String res;
        try {
            MultiValueMap<String, Object> form = new LinkedMultiValueMap<>();
            form.add("name", "test");
            form.add("file", new FileSystemResource(path));
            res = restTemplate.postForObject("/form", form, String.class);
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
            res = restTemplate.postForObject("/form1", form, String.class);
        } finally {
            Files.delete(path);
        }
        Assertions.assertEquals("form1[test]: TEST", res);
    }

    @Test
    public void exception() {
        try {
            String res = restTemplate.getForObject("/exception", String.class);
        } catch (HttpServerErrorException e) {
            Assertions.assertTrue(e.getStatusCode().is5xxServerError());
            Assertions.assertEquals("ERROR", e.getResponseBodyAsString());
        }
    }

}
