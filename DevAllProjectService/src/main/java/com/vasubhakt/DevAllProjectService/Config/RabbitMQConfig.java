package com.vasubhakt.DevAllProjectService.Config;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    
    public static final String exchange = "project.exchange";

    //Queues
    public static final String gitHubQueue = "project.github.queue"; 
    public static final String huggingFaceQueue = "project.huggingface.queue";

    //Routing Keys
    public static final String gitHubKey = "project.github.key";
    public static final String huggingFaceKey = "project.huggingface.key";

    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(exchange);
    }

    @Bean
    public Queue gitHubQueue() {
        return new Queue(gitHubQueue,true);
    }

    @Bean
    public Queue huggingFaceQueue() {
        return new Queue(huggingFaceQueue,true);
    }

    @Bean
    public Binding gitHubBinding(Queue gitHubQueue, TopicExchange exchange) {
        return BindingBuilder.bind(gitHubQueue).to(exchange).with(gitHubKey);
    }

    @Bean
    public Binding huggingFaceBinding(Queue huggingFaceQueue, TopicExchange exchange) {
        return BindingBuilder.bind(huggingFaceQueue).to(exchange).with(huggingFaceKey);
    }

    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public AmqpTemplate amqpTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter());
        return template;
    }
}
