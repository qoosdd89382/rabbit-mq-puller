package com.cherry.rabbitmqpuller;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableRabbit
public class RabbitMqPullerApplication {

	public static void main(String[] args) {
		SpringApplication.run(RabbitMqPullerApplication.class, args);
	}

}
