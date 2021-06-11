package com.example.bemsichat.controller;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ChatController {
    private final String QUEUE_NAME = "chat";

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @GetMapping("/addMessage")
    public String add(@RequestParam String message){
        rabbitTemplate.convertAndSend(QUEUE_NAME,message);
        return "Done!";
    }

    @GetMapping("/receiveMessage")
    public String receive(){
        Object message = rabbitTemplate.receiveAndConvert(QUEUE_NAME);
        return message.toString();
    }

}
