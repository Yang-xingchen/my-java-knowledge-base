package com.example;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
public class WsMvcClient {

    public static void main(String[] args) throws Exception {
        WebSocketClient client = new StandardWebSocketClient();

        AtomicReference<WebSocketSession> s1 = new AtomicReference<>();
        AtomicReference<WebSocketSession> s2 = new AtomicReference<>();
        CompletableFuture<Void> s1Link = client.execute(new WsHandle(), "ws://localhost:8080/ws").thenAccept(s1::set);
        CompletableFuture<Void> s2Link = client.execute(new WsHandle(), "ws://localhost:8080/ws").thenAccept(s2::set);

        CompletableFuture.allOf(s1Link, s2Link).thenAccept(v -> {
            try {
                s1.get().sendMessage(new TextMessage("s1 send"));
                s2.get().sendMessage(new TextMessage("s2 send"));
                TimeUnit.SECONDS.sleep(1);
            } catch (Exception e) {
                throw new RuntimeException(e);
            } finally {
                try {
                    s1.get().close();
                } catch (IOException e) {
                    log.error("close error[{}]: ", s1.get().getId(), e);
                }
                try {
                    s2.get().close();
                } catch (IOException e) {
                    log.error("close error[{}]: ", s2.get().getId(), e);
                }
            }
        }).get();
    }

    public static class WsHandle extends TextWebSocketHandler {

        @Override
        public void afterConnectionEstablished(WebSocketSession session) throws Exception {
            log.info("link: {}", session.getId());
        }

        @Override
        protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
            log.info("receive[{}]: {}", session.getId(), message.getPayload());
        }

        @Override
        public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
            log.info("close: {}", session.getId());
        }

        @Override
        public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
            log.error("error[{}]: ", session.getId(), exception);
        }
    }

}
