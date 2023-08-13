package com.cherry.rabbitmqpuller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

@RestController
public class TestController {

    private final Sender sender;
    private final Puller puller;

    public TestController(
            Sender sender,
            Puller puller
    ) {
        this.sender = sender;
        this.puller = puller;
    }

    @GetMapping("/send")
    public String testSend() {
        sender.send("this is a test message, time=" + java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        return "test MQ sender demo";
    }

    @GetMapping("/pull")
    public String testPull() throws IOException, TimeoutException, InterruptedException {
        puller.pull();
        return "test MQ puller demo";
    }
}