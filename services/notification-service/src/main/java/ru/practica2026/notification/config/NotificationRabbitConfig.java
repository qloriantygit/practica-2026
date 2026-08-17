package ru.practica2026.notification.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;

import org.springframework.amqp.core.AmqpAdmin;

import org.springframework.boot.ApplicationRunner;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class NotificationRabbitConfig {

    public static final String EXCHANGE =
            "notification.events";

    public static final String DLX =
            "notification.events.dlx";

    public static final String REQUEST_QUEUE =
            "notification.requests";

    public static final String DLQ =
            "notification.requests.dlq";

    public static final String REQUEST_ROUTING_KEY =
            "notification.requested";

    public static final String DEAD_ROUTING_KEY =
            "notification.dead";

    @Bean
    public TopicExchange notificationExchange() {
        return new TopicExchange(
                EXCHANGE,
                true,
                false
        );
    }

    @Bean
    public DirectExchange notificationDeadExchange() {
        return new DirectExchange(
                DLX,
                true,
                false
        );
    }

    @Bean
    public Queue notificationRequestQueue() {
        return QueueBuilder
                .durable(REQUEST_QUEUE)
                .deadLetterExchange(DLX)
                .deadLetterRoutingKey(
                        DEAD_ROUTING_KEY
                )
                .build();
    }

    @Bean
    public Queue notificationDeadQueue() {
        return QueueBuilder
                .durable(DLQ)
                .build();
    }

    @Bean
    public Binding notificationRequestBinding(
            Queue notificationRequestQueue,
            TopicExchange notificationExchange
    ) {
        return BindingBuilder
                .bind(notificationRequestQueue)
                .to(notificationExchange)
                .with(REQUEST_ROUTING_KEY);
    }

    @Bean
    public Binding notificationDeadBinding(
            Queue notificationDeadQueue,
            DirectExchange notificationDeadExchange
    ) {
        return BindingBuilder
                .bind(notificationDeadQueue)
                .to(notificationDeadExchange)
                .with(DEAD_ROUTING_KEY);
    }

    @Bean
    public ApplicationRunner declareNotificationTopology(
            AmqpAdmin rabbitAdmin
    ) {
        return args ->
                rabbitAdmin.initialize();
    }
}
