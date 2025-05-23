package com.example.chat_service.repositories;

import com.example.chat_service.entitys.MemberChatroomMapping;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MemberChatroomMappingRepository extends JpaRepository<MemberChatroomMapping, Long> {

    boolean existsByMemberIdAndChatroomId(Long memberId, Long chatroomId);

    void deleteByMemberIdAndChatroomId(Long memberId, Long chatroomId);

    List<MemberChatroomMapping> findAllByMemberId(Long id);

    Optional<MemberChatroomMapping> findByMemberIdAndChatroomId(Long memberId, Long chatroomId);
}
