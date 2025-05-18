깃 허브
https://github.com/bros2024/fastcampus-chat-service

# 에러
인증관련된 에러 : Failed to load resource: the server responded with a status of 403 ()
발생한 이유는 스프링 시큐리티 csrf가 활성화 되어서 그런다 

error:  SyntaxError: Unexpected token '}', ...","member":}]}}]}}]}}"... is not valid JSON
채팅방 생성 api는 정상적으로 처리 되었는데 반환 받은값을 정상적으로 처리를 못했기 때문에 발생 

jakarta.persistence.TransactionRequiredException: No EntityManager with actual transaction available for current thread - cannot reliably process 'remove' call
데이터베이스 에서 데이터를 지우는 액션을 취할려면 Transaction이 필요하다는 에러이다
스레드에 활성화된 트랜잭션이 없기 때문에 발생 ✔ 엔티티 삭제(remove()) 작업을 수행하려면 트랜잭션이 필요 ✔ 즉, 트랜잭션 없이 EntityManager.remove(entity)를 호출했을 때 발생하는 오류

2025-05-12T14:32:15.892+09:00  WARN 14888 --- [chat-service] [nio-8080-exec-7] .w.s.m.s.DefaultHandlerExceptionResolver : Resolved [org.springframework.web.bind.MissingServletRequestParameterException: Required request parameter 'pageable' for method parameter type Pageable is not present]
Pageable을 매개변수로 받아올때는 @Requestparam을 붙이면 안된다

org.hibernate.LazyInitializationException: failed to lazily initialize a collection of role: com.example.chat_service.entitys.Chatroom.memberChatroomMappingSet: could not initialize proxy - no Session
당장 필요하지 않은 데이터 같은경우 lazy 로딩을 활용하여 데이터를 로딩을 한다 그래서 지금 필요한 순간에 로딩을 했는데 지금 entity가 db에 접근을 못하기 때문에 발생하는 에러이다
chatroomdto에 있는 form 메서드에서 chatroom.getMemberChatroomMappingSet().size() 이거 때문에 에러가 발생 


이걸 작성한 이유는 title이 글자가 깨지기 때문에 설정 다른 브라우저에서 채팅방목록 을 조회하면 글자가 깨져서 나와서 사용
server:
    servlet:
        encoding:
            charset: UTF-8 // 기본값 작성 안해도 된다
            enabled: true // 기본값 작성 안해도 된다
            force: true 

# yml 에서 jpa open-in-view: false 로 하는 이유
내가 의도하지 않은 jpa세션을 오랫동안 유지 하므로서 서버에 세션고갈이나 퍼모먼스 이슈가 발생할 수 있기 때문이다 

# 시큐리티 필터 체인 설정 
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