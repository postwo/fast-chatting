package com.example.chat_service.repositories;


import com.example.chat_service.entitys.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    // 기존에 가입된 회원인지 검사
    Optional<Member> findByEmail(String email);
}
