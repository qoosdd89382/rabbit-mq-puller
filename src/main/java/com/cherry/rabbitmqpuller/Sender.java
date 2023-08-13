package com.cherry.rabbitmqpuller;

import com.rabbitmq.client.AMQP;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

@Component
public class Sender {

    @Autowired
    private RabbitTemplate template;

    @Autowired
    private Inspector inspector;

    @Autowired
    private Puller puller;

    public void send(String message) {
        try {
            template.convertAndSend(
                    Constants.EXCHANGE_NAME,
                    Constants.ROUTE_KEY_NAME,
                    message);

//            AMQP.Queue.DeclareOk declareOk = inspector.inspect();
//            int messageCount = declareOk.getMessageCount();
//            int consumerCount = declareOk.getConsumerCount();
//            if (messageCount > 10) {
//                puller.pull();
//            }

            System.out.println(" Sent '" + message + "'");
        } catch (AmqpException e) {
            // Log and handle the exception properly in production environment.
            System.out.println(" [x] Error sending message: " + e.getMessage());
            // TODO: You might want to take further action, such as resending the message after a delay, sending a notification, stopping the application, etc.
//        } catch (IOException | TimeoutException | InterruptedException e) {
//            System.out.println(" [x] Error inspecting message: " + e.getMessage());
//            throw new RuntimeException(e);
        }
    }
}
