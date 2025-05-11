package com.example.chat_service.repositories;

import com.example.chat_service.entitys.Message;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findAllByChatroomId(Long chatroomId);
}
