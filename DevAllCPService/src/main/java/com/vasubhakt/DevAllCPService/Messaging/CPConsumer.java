package com.vasubhakt.DevAllCPService.Messaging;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import com.vasubhakt.DevAllCPService.Model.CpProfile;


@Service
public class CPConsumer {

    @RabbitListener(queues = "${rabbitmq.queue}")
    public void recieveCpProfile(CpProfile profile) {
        System.out.println("Message received from queue: " + profile);
    }
}
