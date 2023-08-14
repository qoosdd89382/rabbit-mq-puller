package com.cherry.rabbitmq.puller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

@RestController
public class TestController {

    private final Puller puller;

    public TestController(
            Puller puller
    ) {
        this.puller = puller;
    }


    @GetMapping("/pull")
    public String testPull() throws IOException, TimeoutException, InterruptedException {
        puller.pull();
        return "test MQ puller demo";
    }
}