package com.example;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketSession;
import org.springframework.web.reactive.socket.client.ReactorNettyWebSocketClient;
import org.springframework.web.reactive.socket.client.WebSocketClient;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.time.Duration;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
public class WsWebfluxClient {

    public static void main(String[] args) throws Exception {
        WebSocketClient client = new ReactorNettyWebSocketClient();

        CountDownLatch latch = new CountDownLatch(2);

        AtomicReference<WebSocketSession> s1 = new AtomicReference<>();
        AtomicReference<WebSocketSession> s2 = new AtomicReference<>();
        client.execute(URI.create("ws://localhost:8080/ws"), new WsHandle(s1, latch)).subscribe();
        client.execute(URI.create("ws://localhost:8080/ws"), new WsHandle(s2, latch)).subscribe();

        latch.await();
        Mono<Void> s1Send = s1.get().send(Mono.just(s1.get().textMessage("s1")));
        Mono<Void> s2Send = s2.get().send(Mono.just(s2.get().textMessage("s2")));
        Mono.zip(s1Send, s2Send).block();

        TimeUnit.SECONDS.sleep(1);
        Mono.zip(s1.get().close(), s2.get().close()).block();
    }

    public static class WsHandle implements WebSocketHandler {

        private final AtomicReference<WebSocketSession> sessionRef;
        private final CountDownLatch latch;

        public WsHandle(AtomicReference<WebSocketSession> sessionRef, CountDownLatch latch) {
            this.sessionRef = sessionRef;
            this.latch = latch;
        }

        @Override
        public Mono<Void> handle(WebSocketSession session) {
            sessionRef.set(session);
            log.info("link: {}", session.getId());
            return session.receive()
                    .doOnSubscribe(subscription -> latch.countDown())
                    .doOnComplete(() -> {
                        log.info("close: {}", session.getId());
                    })
                    .doOnError(throwable -> {
                        log.error("error[{}]: ", session.getId(), throwable);
                    })
                    .doOnNext(message -> {
                        log.info("receive[{}]: {}", session.getId(), message.getPayloadAsText());
                    })
                    .then();
        }

    }

}
