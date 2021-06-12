package com.example.bemsichat.controller;

import com.example.bemsichat.BemsiChatApplication;
import com.example.bemsichat.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.Date;

@Controller
public class ChatController {
//    private final String QUEUE_NAME = "chat";
//
//    @Autowired
//    private RabbitTemplate rabbitTemplate;
//
//    @Autowired
//    private Receiver receiver;

//    @GetMapping("/addMessage")
//    public String add(@RequestParam String message){
//        rabbitTemplate.convertAndSend("spring-boot-exchange", "foo.bar.baz",message);
//        return "Done!";
//    }
//
//    @GetMapping("/receiveMessage")
//    public String receive(){
////        Object message = rabbitTemplate.receiveAndConvert(QUEUE_NAME);
////        return message.toString();
//        return "blanc";
//    }

    @MessageMapping("/chat.sendMessage")
    @SendTo("/topic/javainuse")
    public Message sendMessage(@Payload Message webSocketChatMessage) {
        return webSocketChatMessage;
    }
    @MessageMapping("/chat.newUser")
    @SendTo("/topic/javainuse")
    public Message newUser(@Payload Message webSocketChatMessage,
                                        SimpMessageHeaderAccessor headerAccessor) {
        headerAccessor.getSessionAttributes().put("username", webSocketChatMessage.getSender());
        return webSocketChatMessage;
    }
}
