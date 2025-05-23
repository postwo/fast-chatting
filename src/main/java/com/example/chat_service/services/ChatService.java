package com.example.chat_service.services;

import com.example.chat_service.dtos.ChatroomDto;
import com.example.chat_service.entitys.Chatroom;
import com.example.chat_service.entitys.Member;
import com.example.chat_service.entitys.MemberChatroomMapping;
import com.example.chat_service.entitys.Message;
import com.example.chat_service.repositories.ChatroomRepository;
import com.example.chat_service.repositories.MemberChatroomMappingRepository;
import com.example.chat_service.repositories.MessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatroomRepository chatroomRepository;
    private final MemberChatroomMappingRepository memberChatroomMappingRepository;
    private final MessageRepository messageRepository;

    public Chatroom createChatroom(Member member, String title) { //member =채팅방을 누가 만드는지, title=채팅방의 제목
        Chatroom chatroom = Chatroom.builder()
                .title(title)
                .createdAt(LocalDateTime.now())
                .build();

        chatroom = chatroomRepository.save(chatroom); // 채팅방 생성

        // 채팅방을 만든 자신은 기본적으로 참여하게 한다
        MemberChatroomMapping memberChatroomMapping = chatroom.addMember(member);

        memberChatroomMapping = memberChatroomMappingRepository.save(memberChatroomMapping);

        return chatroom; // 생성된 채팅방 반환
    }


    // 다른 사람이 만든 채팅방에 사용자가 참여하는 메소드
    public Boolean joinChatroom(Member member, Long newChatroomId, Long currentChatroomId) { //currentChatroomId = 어떤 방에서 어떤 방으로 이동 하는지 체크
        if (currentChatroomId != null) { //현재 사용자가 currentChatroomId 방에 참여하고 있다면, 해당 방에서 이동하기 전에 마지막 확인 시간을 기록
            updateLastCheckedAt(member, currentChatroomId);
        }

        if (memberChatroomMappingRepository.existsByMemberIdAndChatroomId(member.getId(), newChatroomId)) {// 현재 참여중인 방인지 체크
            log.info("이미 참여한 채팅방입니다.");
            return false;
        }

        Chatroom chatroom = chatroomRepository.findById(newChatroomId).get();

        // 참여하고 있지 않다면 새로운 참여정보를 저장
        MemberChatroomMapping memberChatroomMapping = MemberChatroomMapping.builder()
                .member(member)
                .chatroom(chatroom)
                .build();

        memberChatroomMapping = memberChatroomMappingRepository.save(memberChatroomMapping);

        return true;
    }

    // 방이동전 마지막 시간 체크
    private void updateLastCheckedAt(Member member, Long currentChatroomId) {
        MemberChatroomMapping memberChatroomMapping = memberChatroomMappingRepository.findByMemberIdAndChatroomId(member.getId(), currentChatroomId)
                .get();
        memberChatroomMapping.updateLastCheckedAt(); // 현재 시간으로 업데이트

        memberChatroomMappingRepository.save(memberChatroomMapping);
    }

    // 이미 참여하고 있는 방을 나오기위한 메소드
    @Transactional
    public Boolean leaveChatroom(Member member, Long chatroomId) {
        // 기존에 참여중인 방인지 아닌지 검사
        if (!memberChatroomMappingRepository.existsByMemberIdAndChatroomId(member.getId(), chatroomId)) {
            log.info("참여하지 않은 방입니다.");
            return false;
        }

        // 참여한 방이면 참여정보를 삭제
        memberChatroomMappingRepository.deleteByMemberIdAndChatroomId(member.getId(), chatroomId);

        return true;
    }

    // 사용자가 참여한 모든 체팅방 목록을 가지고 오는 메소드
    public List<Chatroom> getChatroomList(Member member) {
        List<MemberChatroomMapping> memberChatroomMappingList = memberChatroomMappingRepository.findAllByMemberId(member.getId());

        return memberChatroomMappingList.stream()
                .map(MemberChatroomMapping ->{
                    Chatroom chatroom = MemberChatroomMapping.getChatroom();
                    chatroom.setHasNewMessage(
                            messageRepository.existsByChatroomIdAndCreatedAtAfter(chatroom.getId(), MemberChatroomMapping.getLastCheckedAt()));
                    return chatroom;
                })
                .toList();
    }

    // 메시지 저장
    public Message saveMessage(Member member, Long chatroomId, String text) {
        Chatroom chatroom = chatroomRepository.findById(chatroomId).get(); // 채팅방 존재 확인

        Message message = Message.builder()
                .text(text)
                .member(member)
                .chatroom(chatroom)
                .createdAt(LocalDateTime.now())
                .build();

        return messageRepository.save(message);
    }

    // 메시지 내역 가져오기
    public List<Message> getMessageList(Long chatroomId) {
        return messageRepository.findAllByChatroomId(chatroomId);
    }

    @Transactional(readOnly = true)
    public ChatroomDto getChatroom(Long chatroomId) {
       Chatroom chatroom = chatroomRepository.findById(chatroomId).get();
       return ChatroomDto.from(chatroom);
    }
}
