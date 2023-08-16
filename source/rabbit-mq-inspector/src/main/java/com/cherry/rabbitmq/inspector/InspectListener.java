package com.cherry.rabbitmq.inspector;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class InspectListener {

    private final PullQueueInspector inspector;
    private final PullerNotifier pullerNotifier;

    public InspectListener(
            PullQueueInspector inspector
            , PullerNotifier pullerNotifier
    ) {
        this.inspector = inspector;
        this.pullerNotifier = pullerNotifier;
    }

    @RabbitListener(queues = { Constants.QUEUE_NAME_FOR_INSPECTING })
    public void onMessage(Message message, Channel channel) throws Exception {
        boolean shouldRequeue = true;

        try {
            String receivedMessage = new String(message.getBody());
            System.out.println(" [new] Receiver Received '" + receivedMessage + "'");

            AMQP.Queue.DeclareOk declareOk = inspector.inspectPassiveForPulling();
            int messageCount = declareOk.getMessageCount();
            System.out.println(messageCount);
            if (messageCount > 10) {
                // for watching
                Thread.sleep(1000);

                // TODO: remote invoke
                pullerNotifier.notifyPuller();
            }

            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (Exception e) {
            System.out.println(" [x] Receiver1 error processing message: " + e.getMessage());

            channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, shouldRequeue);
        }
    }
}