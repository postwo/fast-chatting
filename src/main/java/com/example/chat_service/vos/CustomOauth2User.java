package com.example.chat_service.vos;

import com.example.chat_service.entitys.Member;
import lombok.AllArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
public class CustomOauth2User implements OAuth2User {

    Member member;
    Map<String, Object> attributeMap;

    //OAuth2 로그인 사용자뿐만 아니라 일반 로그인 사용자도 CustomOauth2User 객체를 통해 관리할 수 있게 설계
    public Member getMember() {
        return this.member;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return this.attributeMap;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(() -> this.member.getRole());
    }

    @Override
    public String getName() {
        return this.member.getNickName();
    }
}
