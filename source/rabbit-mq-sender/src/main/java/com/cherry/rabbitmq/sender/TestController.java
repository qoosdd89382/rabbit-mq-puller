package com.cherry.rabbitmq.sender;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

@RestController
public class TestController {

    private final Sender sender;

    public TestController(
            Sender sender
    ) {
        this.sender = sender;
    }

    @GetMapping("/send")
    public String testSend() {
        sender.send("this is a test message, time=" + java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        return "test MQ sender demo";
    }

}