package org.app.eventservice.appConfig;


import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;

@Configuration
public class RabbitMQConfig {

    @Value("${rabbitmq.exchange.name}")
    private String exchange;

    @Value("${rabbitmq.queue.event-deletion.name}")
    private String eventDeletionQueue;

    @Value("${rabbitmq.routing.key.event-deletion}")
    private String eventDeletionRoutingKey;

    @Value("${rabbitmq.queue.registration-deletion-status.name}")
    private String registrationDeletionStatusQueue;

    @Value("${rabbitmq.routing.key.registration-deletion-status}")
    private String registrationDeletionStatusRoutingKey;

    @Value("${rabbitmq.queue.registration-restoration.name}")
    private String registrationRestorationQueue;

    @Value("${rabbitmq.routing.key.registration-restoration}")
    private String registrationRestorationRoutingKey;

    @Bean
    public Queue registrationRestorationQueue() {
        return new Queue(registrationRestorationQueue, true);
    }

    @Bean
    public Binding registrationRestorationBinding() {
        return BindingBuilder
                .bind(registrationRestorationQueue())
                .to(exchange())
                .with(registrationRestorationRoutingKey);
    }


    @Bean
    public Queue eventDeletionQueue() {
        return new Queue(eventDeletionQueue, true);
    }

    @Bean
    public Queue registrationDeletionStatusQueue() {
        return new Queue(registrationDeletionStatusQueue, true);
    }

    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(exchange);
    }

    @Bean
    public Binding eventDeletionBinding() {
        return BindingBuilder
                .bind(eventDeletionQueue())
                .to(exchange())
                .with(eventDeletionRoutingKey);
    }

    @Bean
    public Binding registrationDeletionStatusBinding() {
        return BindingBuilder
                .bind(registrationDeletionStatusQueue())
                .to(exchange())
                .with(registrationDeletionStatusRoutingKey);
    }

    @Bean
    public MessageConverter converter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public AmqpTemplate amqpTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(converter());
        return rabbitTemplate;
    }
}
