package com.cherry.rabbitmq.puller;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import jakarta.annotation.PreDestroy;
import org.springframework.amqp.rabbit.connection.Connection;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.TimeoutException;


@Component
@EnableScheduling
public class InnerInspector {

    private final ConnectionFactory connectionFactory;
    private Connection connection;

    private Channel channel;
    private final Puller puller;

    public InnerInspector(
            ConnectionFactory connectionFactory
            , Puller puller
    ) {
        this.connectionFactory = connectionFactory;
        this.connection = connectionFactory.createConnection();
        this.channel = connection.createChannel(false);
        this.puller = puller;
    }

    @Scheduled(cron = "0 * * * * *")
    public void scheduledInspect() throws IOException, TimeoutException, InterruptedException {
        this.connectionCheckAndOpen();

        AMQP.Queue.DeclareOk declareOk = this.inspectPassiveForPulling();
        int messageCount = declareOk.getMessageCount();

        System.out.println(messageCount);

        if (messageCount > 10) {
            this.puller.pull(channel);
        }
    }

    public AMQP.Queue.DeclareOk inspectPassiveForPulling() throws IOException, TimeoutException {
        return this.channel.queueDeclarePassive(Constants.QUEUE_NAME_FOR_PULLING);
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
