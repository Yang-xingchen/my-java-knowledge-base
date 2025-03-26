package com.example;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class WsWebFluxHandle implements WebSocketHandler {

    private static final Map<String, WebSocketSession> SESSION_MAP = new ConcurrentHashMap<>();

    @Override
    public Mono<Void> handle(WebSocketSession session) {
        SESSION_MAP.put(session.getId(), session);
        log.info("link: {}", session.getId());
        return session.receive()
                .doOnComplete(() -> {
                    log.info("close: {}", session.getId());
                    SESSION_MAP.remove(session.getId());
                })
                .doOnError(throwable -> {
                    log.error("error[{}]: ", session.getId(), throwable);
                    SESSION_MAP.remove(session.getId());
                })
                .doOnNext(message -> {
                    log.info("receive[{}]: {}", session.getId(), message.getPayloadAsText());
                    WebSocketMessage msg = session.textMessage(session.getId() + ": " + message.getPayloadAsText());
                    SESSION_MAP.keySet()
                            .stream()
                            .filter(s -> !s.equals(session.getId()))
                            .map(SESSION_MAP::get)
                            .forEach(webSocketSession -> webSocketSession.send(Mono.just(msg)).subscribe());
                })
                .then();
    }

}
