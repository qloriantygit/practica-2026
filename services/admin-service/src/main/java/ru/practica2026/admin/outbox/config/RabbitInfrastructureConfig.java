package ru.practica2026.admin.outbox.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;

import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;

import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitInfrastructureConfig {

    public static final String EXCHANGE =
            "admin.events";

    public static final String DEAD_EXCHANGE =
            "admin.events.dlx";

    public static final String MONITOR_QUEUE =
            "admin.events.monitor";

    public static final String DEAD_QUEUE =
            "admin.events.dlq";

    @Bean
    public ConnectionFactory rabbitConnectionFactory(
            @Value("${RABBITMQ_HOST:localhost}")
            String host,

            @Value("${RABBITMQ_PORT:5672}")
            int port,

            @Value("${RABBITMQ_USER:guest}")
            String username,

            @Value("${RABBITMQ_PASSWORD:guest}")
            String password
    ) {
        CachingConnectionFactory factory =
                new CachingConnectionFactory(
                        host,
                        port
                );

        factory.setUsername(username);
        factory.setPassword(password);

        factory.setPublisherConfirmType(
                CachingConnectionFactory
                        .ConfirmType
                        .CORRELATED
        );

        factory.setPublisherReturns(true);

        return factory;
    }

    @Bean
    public RabbitTemplate rabbitTemplate(
            ConnectionFactory connectionFactory
    ) {
        RabbitTemplate template =
                new RabbitTemplate(
                        connectionFactory
                );

        template.setMandatory(true);

        return template;
    }

    @Bean
    public RabbitAdmin rabbitAdmin(
            ConnectionFactory connectionFactory
    ) {
        RabbitAdmin admin =
                new RabbitAdmin(
                        connectionFactory
                );

        admin.setAutoStartup(true);

        return admin;
    }

    @Bean
    public TopicExchange adminEventsExchange() {
        return new TopicExchange(
                EXCHANGE,
                true,
                false
        );
    }

    @Bean
    public DirectExchange deadExchange() {
        return new DirectExchange(
                DEAD_EXCHANGE,
                true,
                false
        );
    }

    @Bean
    public Queue monitorQueue() {
        return QueueBuilder
                .durable(MONITOR_QUEUE)
                .deadLetterExchange(
                        DEAD_EXCHANGE
                )
                .deadLetterRoutingKey(
                        "admin.dead"
                )
                .build();
    }

    @Bean
    public Queue deadQueue() {
        return QueueBuilder
                .durable(DEAD_QUEUE)
                .build();
    }

    @Bean
    public Binding monitorBinding(
            Queue monitorQueue,
            TopicExchange adminEventsExchange
    ) {
        return BindingBuilder
                .bind(monitorQueue)
                .to(adminEventsExchange)
                .with("admin.#");
    }

    @Bean
    public Binding deadBinding(
            Queue deadQueue,
            DirectExchange deadExchange
    ) {
        return BindingBuilder
                .bind(deadQueue)
                .to(deadExchange)
                .with("admin.dead");
    }

    @Bean
    public ApplicationRunner declareRabbitTopology(
            RabbitAdmin rabbitAdmin
    ) {
        return args ->
                rabbitAdmin.initialize();
    }
}
