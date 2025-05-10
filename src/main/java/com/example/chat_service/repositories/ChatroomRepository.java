package com.example.chat_service.repositories;

import com.example.chat_service.entitys.Chatroom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatroomRepository extends JpaRepository<Chatroom, Long> {
}
