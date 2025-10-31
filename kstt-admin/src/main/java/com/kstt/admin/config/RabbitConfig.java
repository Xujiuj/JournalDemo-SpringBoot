package com.kstt.admin.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kstt.admin.config.properties.AppProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;

/**
 * RabbitMQ 配置
 */
@Configuration
@EnableRabbit
@RequiredArgsConstructor
public class RabbitConfig {

    private final AppProperties appProperties;

    @Bean
    public TopicExchange articleStatusExchange() {
        AppProperties.RabbitMqProperties.ArticleStatusProperties config = appProperties.getRabbitmq().getArticleStatus();
        return new TopicExchange(config.getExchange(), config.isDurable(), false);
    }

    @Bean
    public Queue articleStatusQueue() {
        AppProperties.RabbitMqProperties.ArticleStatusProperties config = appProperties.getRabbitmq().getArticleStatus();
        return config.isDurable() 
            ? QueueBuilder.durable(config.getQueue()).build() 
            : QueueBuilder.nonDurable(config.getQueue()).build();
    }

    @Bean
    public Binding articleStatusBinding(Queue articleStatusQueue, TopicExchange articleStatusExchange) {
        String routingKey = appProperties.getRabbitmq().getArticleStatus().getRoutingKey();
        return BindingBuilder.bind(articleStatusQueue).to(articleStatusExchange).with(routingKey);
    }

    @Bean
    public MessageConverter rabbitMessageConverter() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
        return new Jackson2JsonMessageConverter(objectMapper);
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, MessageConverter rabbitMessageConverter) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(rabbitMessageConverter);
        template.setMandatory(true);
        return template;
    }
}
