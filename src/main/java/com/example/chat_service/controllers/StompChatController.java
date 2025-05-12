package com.example.chat_service.controllers;

import com.example.chat_service.dtos.ChatMessage;
import com.example.chat_service.dtos.ChatroomDto;
import com.example.chat_service.entitys.Message;
import com.example.chat_service.services.ChatService;
import com.example.chat_service.vos.CustomOauth2User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.util.Map;

@Slf4j
@Controller
@RequiredArgsConstructor
public class StompChatController {

    private final ChatService chatService;
    private final SimpMessagingTemplate messagingTemplate;

    // /pub/chats 이 경로로 발행된 메시지들을 여기 @MessageMapping("/chats")  로 전달한다
    // /pub/chats여기서 pub은 생략하고 브로커가 뒤에 하위경로에 chats메 맞는곳으로 라우팅 해준다
    @MessageMapping("/chats/{chatroomId}")
    @SendTo("/sub/chats/{chatroomId}") // 여기 return message를 구독자(sub/chats를 구독하고 있는 사람들)들에게로 전달
    public ChatMessage handleMessage(Principal principal, @DestinationVariable Long chatroomId, @Payload Map<String,String> payload){
        log.info("{} received {} in {}", principal.getName(),payload ,chatroomId);
        CustomOauth2User user = (CustomOauth2User) ((AbstractAuthenticationToken) principal).getPrincipal();
        Message message = chatService.saveMessage(user.getMember(), chatroomId, payload.get("message"));
        messagingTemplate.convertAndSend("/sub/chats/updates", chatService.getChatroom(chatroomId)); //채팅방에 새로운 메시지를 발행

        return new ChatMessage(principal.getName(),payload.get("message")); // 다른 클라이언트에게 전달
    }
}
