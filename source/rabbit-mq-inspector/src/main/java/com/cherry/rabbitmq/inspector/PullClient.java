package com.cherry.rabbitmq.inspector;

import feign.RequestLine;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

public interface PullClient {
    @RequestLine("GET /pull")
    String pull();
}
