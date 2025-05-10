package com.example.chat_service;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfiguration {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(request -> request.anyRequest().authenticated()) // 서버로 들어오는 요청은 모두 인증이 필요하다
                .oauth2Login(Customizer.withDefaults()) // oauth로그인을 하겠
                .csrf(csrf -> csrf.disable());

        return http.build();
    }
}
