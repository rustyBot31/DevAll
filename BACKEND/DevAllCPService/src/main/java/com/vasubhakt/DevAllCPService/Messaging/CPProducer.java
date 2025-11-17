package com.vasubhakt.DevAllCPService.Messaging;

import java.util.Optional;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.stereotype.Service;

import com.vasubhakt.DevAllCPService.Config.RabbitMQConfig;
import com.vasubhakt.DevAllCPService.Model.CPFetchRequest;
import com.vasubhakt.DevAllCPService.Model.CpProfile;
import com.vasubhakt.DevAllCPService.Repo.CpProfileRepo;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CPProducer {

    private final AmqpTemplate amqpTemplate;
    private final CpProfileRepo cpRepo;
    
    public void sendFetchRequest(CPFetchRequest request) {
        Optional<CpProfile> optionalProfile = cpRepo.findByUsername(request.getUsername());
        if(optionalProfile.isEmpty()) {
            throw new RuntimeException("User does not exist");
        }
        String routingKey = switch(request.getPlatform().toLowerCase()) {
            case "codeforces" -> RabbitMQConfig.cfKey;
            case "leetcode" -> RabbitMQConfig.lcKey;
            case "codechef" -> RabbitMQConfig.ccKey;
            case "atcoder" -> RabbitMQConfig.acKey;
            default -> throw new IllegalArgumentException("Unsupported platform: " + request.getPlatform());
        };

        amqpTemplate.convertAndSend(RabbitMQConfig.exchange, routingKey, request);
        System.out.println("📤 Sent fetch request for " + request.getPlatform() + " handle: " + request.getHandle());
    }

}
