package com.vasubhakt.DevAllCPService.Messaging;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import com.vasubhakt.DevAllCPService.Config.RabbitMQConfig;
import com.vasubhakt.DevAllCPService.Model.CpProfile;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CPProducer {
    
    private final RabbitTemplate rabbitTemplate;

    public void sendCpProfile(CpProfile profile) {
        rabbitTemplate.convertAndSend(RabbitMQConfig.exchange, RabbitMQConfig.routingKey, profile);
        System.out.println("Message sent to queue: " + profile);
    } 

}
