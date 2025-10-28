package com.vasubhakt.DevAllCPService.Config;

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

    public static final String exchange = "cp.exchange";

    //Queues
    public static final String cfQueue = "cp.cf.queue";
    public static final String lcQueue = "cp.lc.queue";
    public static final String ccQueue = "cp.cc.queue";
    public static final String acQueue = "cp.ac.queue";

    //Routing Keys
    public static final String cfKey = "cp.cf.key";
    public static final String lcKey = "cp.lc.key";
    public static final String ccKey = "cp.cc.key";
    public static final String acKey = "cp.ac.key";

    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(exchange);
    }

    @Bean
    public Queue CFQueue() {
        return new Queue(cfQueue,true);
    }

    @Bean
    public Queue LCQueue() {
        return new Queue(lcQueue,true);
    }

    @Bean
    public Queue CCQueue() {
        return new Queue(ccQueue,true);
    }

    @Bean
    public Queue ACQueue() {
        return new Queue(acQueue,true);
    }

    @Bean
    public Binding cfBinding(Queue CFQueue, TopicExchange exchange) {
        return BindingBuilder.bind(CFQueue).to(exchange).with(cfKey);
    }

    @Bean
    public Binding lcBinding(Queue LCQueue, TopicExchange exchange) {
        return BindingBuilder.bind(LCQueue).to(exchange).with(lcKey);
    }

    @Bean
    public Binding ccBinding(Queue CCQueue, TopicExchange exchange) {
        return BindingBuilder.bind(CCQueue).to(exchange).with(ccKey);
    }

    @Bean
    public Binding acBinding(Queue ACQueue, TopicExchange exchange) {
        return BindingBuilder.bind(ACQueue).to(exchange).with(acKey);
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