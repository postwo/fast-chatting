package com.example.chat_service.controllers;

import com.example.chat_service.dtos.ChatMessage;
import com.example.chat_service.entitys.Chatroom;
import com.example.chat_service.services.ChatService;
import com.example.chat_service.vos.CustomOauth2User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/chats")
@RestController
public class ChatController {

    private final ChatService chatService;

    // 채팅방 생성
    @PostMapping
    public Chatroom createChatroom(@AuthenticationPrincipal CustomOauth2User user, @RequestParam String title) {
        return chatService.createChatroom(user.getMember(), title);
    }

    // 방 참여
    @PostMapping("/{chatroomId}")
    public Boolean joinChatroom(@AuthenticationPrincipal CustomOauth2User user, @PathVariable Long chatroomId) {
        return chatService.joinChatroom(user.getMember(), chatroomId);
    }

    // 채티방 나가기
    @DeleteMapping("/{chatroomId}")
    public Boolean leaveChatroom(@AuthenticationPrincipal CustomOauth2User user, @PathVariable Long chatroomId) {
        return chatService.leaveChatroom(user.getMember(), chatroomId);
    }

    // 저체 채팅 방 목록
    @GetMapping
    public List<Chatroom> getChatroomList(@AuthenticationPrincipal CustomOauth2User user) {
        return chatService.getChatroomList(user.getMember());
    }


}
