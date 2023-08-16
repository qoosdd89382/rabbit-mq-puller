package com.cherry.rabbitmq.inspector;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import jakarta.annotation.PreDestroy;
import org.springframework.amqp.rabbit.connection.Connection;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.TimeoutException;


@Component
public class PullQueueInspector {


    private final ConnectionFactory connectionFactory;
    private Connection connection;
    private Channel channel;

    public PullQueueInspector(
            ConnectionFactory connectionFactory
    ) {
        this.connectionFactory = connectionFactory;
        this.connection = connectionFactory.createConnection();
        this.channel = connection.createChannel(false);
    }

    public void connectionCheckAndOpen() {
        if (this.channel != null) {
            return;
        }
        if (this.connection == null) {
            this.connection = connectionFactory.createConnection();
        }
        this.channel = connection.createChannel(false);
    }

    AMQP.Queue.DeclareOk inspectPassiveForPulling() throws IOException, TimeoutException {
        this.connectionCheckAndOpen();
        return this.channel.queueDeclarePassive(Constants.QUEUE_NAME_FOR_PULLING);
    }

    // 類的銷毀方法，在銷毀時關閉連線和通道
    @PreDestroy
    public void closeConnection() throws IOException, TimeoutException {
        if (this.channel != null) {
            this.channel.close();
        }
        if (this.connection != null) {
            this.connection.close();
        }
    }
}