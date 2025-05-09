package com.example.chat_service.configs;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@EnableWebSocketMessageBroker // STOMP 기반 웹소켓 메시지 브로커 활성화
@Configuration // Spring 설정 클래스임을 나타냄
public class StompConfiguration implements WebSocketMessageBrokerConfigurer {

    // 클라이언트가 WebSocket에 연결할 수 있는 엔드포인트를 설정하는 메서드
    // 엔드포인트= 클라이언트(WebSocket 사용자)가 서버에 연결할 때 사용하는 URL ✔ 예를 들어, /stomp/chats → 클라이언트가 웹소켓 연결을 열 때 이 경로를 사용
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/stomp/chats");
        // 클라이언트는 ws://서버주소/stomp/chats 로 연결 가능
    }

    // 메시지 브로커 설정
    // 메시지 브로커 = 클라이언트가 메시지를 보낼 때 → 어디로 보내고 누구에게 전달할지 결정
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // 클라이언트에서 메시지를 보낼 때 사용할 prefix 설정
        registry.setApplicationDestinationPrefixes("/pub"); // 예: "/pub/chat"

        // 클라이언트가 구독할 수 있는 메시지 브로커 활성화
        registry.enableSimpleBroker("/sub"); // 예: "/sub/chat"
    }
}
