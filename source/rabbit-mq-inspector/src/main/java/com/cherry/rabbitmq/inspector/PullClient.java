package com.cherry.rabbitmq.inspector;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "PullClient")
public interface PullClient {
    @GetMapping("/pull")
    String pull();

}
