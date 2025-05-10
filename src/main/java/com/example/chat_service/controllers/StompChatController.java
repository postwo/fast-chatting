package com.example.chat_service.controllers;

import com.example.chat_service.dtos.ChatMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.util.Map;

@Slf4j
@Controller
public class StompChatController {

    // /pub/chats 이 경로로 발행된 메시지들을 여기 @MessageMapping("/chats")  로 전달한다
    // /pub/chats여기서 pub은 생략하고 브로커가 뒤에 하위경로에 chats메 맞는곳으로 라우팅 해준다
    @MessageMapping("/chats/{chatroomId}")
    @SendTo("/sub/chats/{chatroomId}") // 여기 return message를 구독자(sub/chats를 구독하고 있는 사람들)들에게로 전달
    public ChatMessage handleMessage(@AuthenticationPrincipal Principal principal, @DestinationVariable Long chatroomId, @Payload Map<String,String> payload){
        log.info("{} received {} in {}", principal.getName(),payload ,chatroomId);

        return new ChatMessage(principal.getName(),payload.get("message")); // 다른 클라이언트에게 전달
    }
}
