package com.example;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class WsMvcHandle extends TextWebSocketHandler {

    private static final Map<String, WebSocketSession> SESSION_MAP = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        SESSION_MAP.put(session.getId(), session);
        log.info("link: {}", session.getId());
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        log.info("receive[{}]: {}", session.getId(), message.getPayload());
        TextMessage msg = new TextMessage(session.getId() + ": " + message.getPayload());
        SESSION_MAP.keySet()
                .stream()
                .filter(s -> !s.equals(session.getId()))
                .map(SESSION_MAP::get)
                .forEach(webSocketSession -> {
                    try {
                        webSocketSession.sendMessage(msg);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        log.info("close: {}", session.getId());
        SESSION_MAP.remove(session.getId());
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        log.error("error[{}]: ", session.getId(), exception);
        SESSION_MAP.remove(session.getId());
    }

}
