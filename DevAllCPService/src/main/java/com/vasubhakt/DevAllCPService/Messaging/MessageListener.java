package com.vasubhakt.DevAllCPService.Messaging;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;


@Service
public class MessageListener {

    @RabbitListener(queues = "${rabbitmq.queue}")
    public void consume(String message) {
        System.out.println("Message received from queue: " + message);
    }
}
