package com.example.chat_service.controllers;

import com.example.chat_service.dtos.ChatMessage;
import com.example.chat_service.dtos.ChatroomDto;
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
    //Chatroom은 entity 이기 떄문에 이걸로 반환하면 연관관계 때문에 ajax에서 데이터 처리르 못한다 그러므로 dto를 만들어서 처리
    public ChatroomDto createChatroom(@AuthenticationPrincipal CustomOauth2User user, @RequestParam String title) {
         Chatroom chatroom = chatService.createChatroom(user.getMember(), title);

         return ChatroomDto.from(chatroom);
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

    // 전체 채팅 방 목록
    @GetMapping
    public List<ChatroomDto> getChatroomList(@AuthenticationPrincipal CustomOauth2User user) {
        List<Chatroom> chatroomList= chatService.getChatroomList(user.getMember());

        return chatroomList.stream()
                .map(ChatroomDto::from)
                .toList();
    }


}
