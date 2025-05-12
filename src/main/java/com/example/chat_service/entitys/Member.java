package com.example.chat_service.entitys;

import com.example.chat_service.enums.Gender;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;

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


    public void updatePassword(String password, String confirmedPassword, PasswordEncoder passwordEncoder) {
        if(!password.equals(confirmedPassword)) {
            throw new IllegalArgumentException("패스워드가 일치하지 않습니다.");
        }

        this.password = passwordEncoder.encode(password);
    }
}
