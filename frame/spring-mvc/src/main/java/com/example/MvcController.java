package com.example;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Data;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.io.Serializable;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

@SuppressWarnings("JavadocLinkAsPlainText")
@RestController
public class MvcController {

    /**
     * 无参数
     * http://localhost:8080/base
     */
    @GetMapping("/base")
    public String base() {
        return "base";
    }

    /**
     * 无注解，使用参数
     * http://localhost:8080/entry?key=k&value=v
     */
    @GetMapping("/entry")
    public String entry(Entry entry) {
        return "entry: " + entry.getKey() + "/" + entry.getValue();
    }

    /**
     * 无注解，使用路径参数
     * http://localhost:8080/entry/k/v
     */
    @GetMapping("/entry/{key}/{value}")
    public String entry1(Entry entry) {
        return "entry: " + entry.getKey() + "/" + entry.getValue();
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
    public String param(@RequestParam("param") String param) {
        return "param: " + param;
    }

    /**
     * 可选，使用{@link Optional}代替{@link RequestParam#required()}
     * http://localhost:8080/param1?param1=param1
     * http://localhost:8080/param1
     */
    @GetMapping("/param1")
    public String param1(@RequestParam("param1") Optional<String> optional) {
        return optional.map(param -> "param1: " + param).orElse("param1");
    }

    /**
     * 路径参数
     * http://localhost:8080/uriPattern/param
     */
    @GetMapping("/uriPattern/{param}")
    public String uriPattern(@PathVariable("param") String param) {
        return "uriPattern: " + param;
    }

    /**
     * 路径参数可用正则
     * http://localhost:8080/uriRegex/param
     */
    @GetMapping("/uriRegex/{param:[a-zA-Z]*}")
    public String uriRegex(@PathVariable("param") String param) {
        return "uriRegex: " + param;
    }

    /**
     * http://localhost:8080/uriRegex/123
     */
    @GetMapping("/uriRegex/{param:[0-9]*}")
    public String uriRegex1(@PathVariable("param") Integer param) {
        return "uriRegex1: " + param;
    }

    /**
     * 矩阵
     * http://localhost:8080/matrix/path;a=a;b=b
     */
    @GetMapping("/matrix/{path}")
    public String matrix(@MatrixVariable("a") String a, @MatrixVariable("b") String b) {
        return "matrix: a=" + a + ", b=" + b;
    }

    /**
     * http://localhost:8080/matrix1/path;a=a;b=b/path1;a=a1;b=b1
     */
    @GetMapping("/matrix1/{path}/{path1}")
    public String matrix1(@MatrixVariable(value = "a", pathVar = "path") String a,
                          @MatrixVariable(value = "a", pathVar = "path1") String a1,
                          @MatrixVariable(value = "b", pathVar = "path") String b,
                          @MatrixVariable(value = "b", pathVar = "path1") String b1) {
        return "matrix: a=" + a + ", a1=" + a1 + ", b=" + b + ", b1=" + b1;
    }

    /**
     * http://localhost:8080/matrix2/path;a=a;b=b/path1;a=a1;b=b1
     */
    @GetMapping("/matrix2/{path}/{path1}")
    public String matrix2(@MatrixVariable MultiValueMap<String, String> map) {
        // matrix: {a=[a, a1], b=[b, b1]}
        return "matrix: " + map;
    }

    /**
     * http头
     * http://localhost:8080/head
     */
    @GetMapping("/head")
    public String head(@RequestHeader("myHead") String host) {
        return "head[myHead]: " + host;
    }

    /**
     * cookie
     * http://localhost:8080/cookie
     */
    @GetMapping("/cookie")
    public String cookie(@CookieValue("cookie") String cookie) {
        return "cookie[cookie]: " + cookie;
    }

    /**
     * {@link HttpServletRequest}原始请求
     * http://localhost:8080/request
     */
    @GetMapping("/request")
    public String request(HttpServletRequest request) {
        return "request: " + request.getMethod();
    }

    /**
     * {@link HttpServletResponse}原始响应
     * http://localhost:8080/response
     */
    @GetMapping("/response")
    public void response(HttpServletResponse response) throws IOException {
        ServletOutputStream outputStream = response.getOutputStream();
        outputStream.print("response");
        outputStream.flush();
    }

    /**
     * 返回json
     * 注意: 需要{@link ResponseBody}或在类上使用{@link RestController}
     * http://localhost:8080/retEntry
     */
    @GetMapping("/retEntry")
    public Entry retEntry() {
        Entry entry = new Entry();
        entry.setKey("key");
        entry.setValue("value");
        return entry;
    }

    /**
     * 长连接
     * http://localhost:8080/deferred
     */
    @GetMapping("/deferred")
    public DeferredResult<String> deferred() {
        DeferredResult<String> result = new DeferredResult<>();
        AtomicLong ret = new AtomicLong();
        Thread.ofVirtual().start(() -> {
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } finally {
                result.setResult(Long.toString(System.currentTimeMillis() - ret.get()));
            }
        });
        ret.set(System.currentTimeMillis());
        return result;
    }

    /**
     * 分多次输出(一次返回)
     * http://localhost:8080/stream
     */
    @GetMapping("/stream")
    public ResponseBodyEmitter stream() {
        ResponseBodyEmitter emitter = new ResponseBodyEmitter();
        Thread.ofVirtual().start(() -> {
            try {
                for (int i = 0; i < 30; i++) {
                    emitter.send("send[" + i + "]: " + System.currentTimeMillis() + "\n");
                    TimeUnit.MILLISECONDS.sleep(50);
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            } finally {
                emitter.complete();
            }
        });
        return emitter;
    }

    /**
     * 分多次输出(多次返回)
     * http://localhost:8080/sse
     */
    @GetMapping("/sse")
    public SseEmitter sse() {
        SseEmitter emitter = new SseEmitter();
        Thread.ofVirtual().start(() -> {
            try {
                for (int i = 0; i < 30; i++) {
                    emitter.send("send[" + i + "]: " + System.currentTimeMillis());
                    TimeUnit.MILLISECONDS.sleep(50);
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            } finally {
                emitter.complete();
            }
        });
        return emitter;
    }

    /**
     *
     */
    @PostMapping("/body")
    public String body(@RequestBody String body) {
        return "body: " + body;
    }

    /**
     * 表单处理
     * http://localhost:8080/form
     */
    @PostMapping("/form")
    public String form(@RequestParam("name") String name, @RequestParam("file") MultipartFile file) throws IOException {
        return "form[" + name + "]: " + new String(file.getBytes());
    }

    /**
     * 表单处理
     * http://localhost:8080/form1
     */
    @PostMapping("/form1")
    public String form1(MyForm form) throws IOException {
        return "form1[" + form.name + "]: " + new String(form.file.getBytes());
    }

    @Data
    public static class MyForm implements Serializable {
        private String name;
        private MultipartFile file;
    }

    /**
     * 异常处理{@link #error(MyException)}
     * http://localhost:8080/exception
     */
    @GetMapping("/exception")
    public String exception() {
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
