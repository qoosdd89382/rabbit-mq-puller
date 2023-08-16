package com.cherry.rabbitmq.puller;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.rabbit.connection.Connection;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import com.rabbitmq.client.GetResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

@Component
public class Puller {

    private final ConnectionFactory connectionFactory;

    public Puller(
            ConnectionFactory connectionFactory
    ) {
        this.connectionFactory = connectionFactory;
    }

    public void pull(Channel channel) throws IOException, TimeoutException, InterruptedException {
        try (channel) {
            boolean hasResponse = this.pullMessage(channel);
            while (hasResponse) {
                hasResponse = this.pullMessage(channel);
            }

        }
    }

    public void pull() throws IOException, TimeoutException, InterruptedException {
        try (Connection connection = connectionFactory.createConnection();
             Channel channel = connection.createChannel(false);
        ) {

            boolean hasResponse = this.pullMessage(channel);
            while (hasResponse) {
                hasResponse = this.pullMessage(channel);
            }

        }
    }

    private boolean pullMessage(Channel channel) throws IOException {
        GetResponse response = channel.basicGet(Constants.QUEUE_NAME_FOR_PULLING, true);
        boolean hasResponse = response != null;
        if (hasResponse) {
            String message = new String(response.getBody());
            System.out.println("Pulled: " + message);
        } else {
            System.out.println("No messages available");
            System.out.println("Consumer turns off");
        }
        return hasResponse;
    }

}