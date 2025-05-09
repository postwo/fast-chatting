package com.example.chat_service.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Slf4j
@Controller
public class StompChatController {

    // /pub/chats 이 경로로 발행된 메시지들을 여기 @MessageMapping("/chats")  로 전달한다
    // /pub/chats여기서 pub은 생략하고 브로커가 뒤에 하위경로에 chats메 맞는곳으로 라우팅 해준다
    @MessageMapping("/chats")
    @SendTo("/sub/chats") // 여기 return message를 구독자(sub/chats를 구독하고 있는 사람들)들에게로 전달
    public String handleMessage(@Payload String message){
        log.info("{} received", message);

        return message;
    }
}
