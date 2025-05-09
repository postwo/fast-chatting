package com.example.chat_service.services;

import com.example.chat_service.entitys.Member;
import com.example.chat_service.enums.Gender;
import com.example.chat_service.repositories.MemberRepository;
import com.example.chat_service.vos.CustomOauth2User;
import lombok.RequiredArgsConstructor;
import org.springframework.cglib.core.Local;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class CustomOauth2Service extends DefaultOAuth2UserService {

    final MemberRepository memberRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        Map<String, Object> attributeMap = oAuth2User.getAttribute("kakao_account");
        String email = (String) attributeMap.get("email");
        Member member = memberRepository.findByEmail(email)
                .orElseGet(() ->  // 값이 없을경우에 회원 등록
                    registerMember(attributeMap)
                );

        return new CustomOauth2User(member, oAuth2User.getAttributes());
    }

    private Member registerMember(Map<String, Object> attributeMap) {
        Member member= Member.builder()
                .email((String) attributeMap.get("email"))
                .nickName((String) ((Map) attributeMap.get("profile")).get("nickname")) // 프로파일 하위에 있어서 이렇게 가져와야 한다
                .name((String) attributeMap.get("name"))
                .phoneNumber((String) attributeMap.get("phoneNumber"))
                .gender(Gender.valueOf(((String) attributeMap.get("gender")).toUpperCase()))
                .birthDay(getBirthDay(attributeMap))
                .role("USER_ROLE")
                .build();

        return memberRepository.save(member);
    }

    private LocalDate getBirthDay(Map<String, Object> attributeMap) {
        String birthYear = (String) attributeMap.get("birthyear");
        String birthDay = (String) attributeMap.get("birthday");

        return LocalDate.parse(birthYear + birthDay, DateTimeFormatter.BASIC_ISO_DATE); //LocalDate 형태로 변환후 반환
    }
}
