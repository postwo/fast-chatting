package com.example.chat_service.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfiguration {

    // security필터 체인 2가지를 스프링에 bean으로 등록하면 어떤게 먼저 사용될지 보장이 안되기 때문에 그래서 내가 원하는것이 먼저 적용 될 수 있게 하는게 좋다
    // 우선순위를 잡는거는 둘중에 어떤게 더 구체적인 대상을 가지고 있는지 확인 후 우선 실행되게 하는게 좋다
    // 어떤게 먼저 실행되었는지 확인할려면 시큐리티 자체 formlogin이 뜨는지 확인하면 된다
    /*securityMatcher("/consultants/**", "/login")처럼 특정 경로가 명확하게 설정된 필터 체인은 우선적으로 실행해야 합니다.
     그렇지 않으면, 일반적인 securityFilterChain이 먼저 실행되어 모든 요청(anyRequest())을 인증 처리하게 되고,
     특정 경로에 대한 별도 보안 설정이 적용되지 않을 가능성이 있습니다.*/

    /* 만약에 @Bean에 두개다 securityMatcher가 설정 되어있으면
    * ex) .securityMatcher("/admin/dashboard") , .securityMatcher("/admin/**")
    * 여기서 더 우선 순위를 갖는것은 securityMatcher("/admin/dashboard") 이렇게 명확하게 작성한게 우선 된다
    * */


    @Order(2) // 1실행후 실행
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(request -> request.anyRequest().authenticated()) // 서버로 들어오는 요청은 모두 인증이 필요하다
                .oauth2Login(Customizer.withDefaults()) // oauth로그인을 하겠다
                .csrf(csrf -> csrf.disable());

        return http.build();
    }

    // 상담사 로그인설정
    @Order(1) // 먼저 실행된다
    @Bean
    public SecurityFilterChain consultantSecurityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .securityMatcher("/consultants/**","/login")
                .authorizeHttpRequests(request -> request
                        .requestMatchers(HttpMethod.POST, "/consultants").permitAll()
                        .anyRequest().hasRole("CONSULTANT"))
                 .formLogin(Customizer.withDefaults())
                .csrf(csrf -> csrf.disable());

        return httpSecurity.build();
    }

    @Bean
    public PasswordEncoder  passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
