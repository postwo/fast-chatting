package com.example.chat_service.entitys;

import com.example.chat_service.enums.Gender;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Member { // 카카오에서 받아오는 데이터랑 맞춘거다

    @Id
    @Column(name="member_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    String email;
    String nickName;
    String name;
    String password;
    @Enumerated(EnumType.STRING)
    Gender gender;
    String phoneNumber;
    LocalDate birthDay;
    String role;


}
