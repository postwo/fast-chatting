package com.example.chat_service.handlers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class WebSocketChatHandler extends TextWebSocketHandler {

    // 웹소켓 연결된 클라이언트 목록을 저장하는 Map
    // ConcurrentHashMap =멀티 쓰레드 환경에서 동시 접근 가능
    // hashmap = 단일 쓰레드 환경에서 사용
    final Map<String, WebSocketSession> webSocketSessionMap = new ConcurrentHashMap<>();

    // 웹소켓 클라이언트가 서버로 연결한 이후에 실행
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        log.info("{} connected", session.getId());

        this.webSocketSessionMap.put(session.getId(), session);
    }

    // 웹소켓 클라이언트로부터 메시지를 받은 이후에 실행
    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        log.info("{} sent {}", session.getId(),message.getPayload());
        this.webSocketSessionMap.values().forEach(webSocketSession -> {
            try {
                webSocketSession.sendMessage(message);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    // 웹소켓 클라이언트와의 연결을 끝내는 이후에 실행
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        log.info("{} disconnected", session.getId());

        this.webSocketSessionMap.remove(session.getId());
    }
}
