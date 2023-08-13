package com.cherry.rabbitmqpuller;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.connection.Connection;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

@Component
public class Inspector {

    private final ConnectionFactory connectionFactory;
    private final Puller puller;

    public Inspector(
            ConnectionFactory connectionFactory
            , Puller puller
    ) {
        this.connectionFactory = connectionFactory;
        this.puller = puller;
    }

    @RabbitListener(queues = { Constants.QUEUE_NAME_FOR_INSPECTING })
    public void onMessage(Message message, Channel channel) throws Exception {
        boolean shouldRequeue = true;

        try {
            String receivedMessage = new String(message.getBody());
            System.out.println(" [new] Receiver Received '" + receivedMessage + "'");

            AMQP.Queue.DeclareOk declareOk = this.inspectPassiveForPulling();
            int messageCount = declareOk.getMessageCount();
            System.out.println(messageCount);
            if (messageCount > 10) {
                // for watching
                Thread.sleep(1000);

                // TODO: remote invoke
                puller.pull();
            }

            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (Exception e) {
            System.out.println(" [x] Receiver1 error processing message: " + e.getMessage());

            channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, shouldRequeue);
        }
    }

    public AMQP.Queue.DeclareOk inspectPassiveForPulling() throws IOException, TimeoutException {
        try (Connection connection = connectionFactory.createConnection();
             Channel channel = connection.createChannel(false);
        ) {
            return channel.queueDeclarePassive(Constants.QUEUE_NAME_FOR_PULLING);
        }
    }

    public static AMQP.Queue.DeclareOk inspectPassiveForPulling(Channel channel) throws IOException, TimeoutException {
        try (channel) {
            return channel.queueDeclarePassive(Constants.QUEUE_NAME_FOR_PULLING);
        }
    }
}