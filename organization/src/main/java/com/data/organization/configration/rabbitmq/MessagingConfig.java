package com.data.organization.configration.rabbitmq;

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
public class MessagingConfig {

    public static final String QUEUE1 = "data_consume";
    public static final String QUEUE2 = "data_produce";
    public static final String EXCHANGE = "record_data_exchange";
    public static final String ROUTING_KEY = "org_data";

    @Bean
    public Queue dataQueue() {
        return new Queue(QUEUE1);
    }

    @Bean
    public Queue dataTypeQueue() {
        return new Queue(QUEUE2);
    }

    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(EXCHANGE);
    }

    @Bean
    public Binding dataBinding() {
        return BindingBuilder
                .bind(dataQueue())
                .to(exchange())
                .with(ROUTING_KEY);
    }

     @Bean
    public Binding dataTypeBinding() {
        return BindingBuilder
                .bind(dataTypeQueue())
                .to(exchange())
                .with(ROUTING_KEY);
    }

    @Bean
    public MessageConverter converter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public AmqpTemplate template(ConnectionFactory connectionFactory) {
        final RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(converter());
        return rabbitTemplate;
    }
}
