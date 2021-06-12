package com.example.bemsichat.controller;

import com.example.bemsichat.model.ChatMessage;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
public class ChatController {
    private final String QUEUE_NAME = "chat";

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @PostMapping(path = "/addMessage", consumes = "application/json", produces = "application/json")
    public void add(@RequestBody ChatMessage message){
        rabbitTemplate.convertAndSend(QUEUE_NAME,message);
    }

    @GetMapping(path = "/receiveMessage", produces = "application/json")
    public List<ChatMessage> receive(){
        List<ChatMessage> messages = new ArrayList<>();
        while(true){
            ChatMessage message = (ChatMessage) rabbitTemplate.receiveAndConvert(QUEUE_NAME);
            if (message == null){
                return messages;
            }
            messages.add(message);
        }
    }
}
