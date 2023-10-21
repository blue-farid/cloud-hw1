package com.bluefarid.cloud.bank.bankoperator.integration;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.retry.annotation.CircuitBreaker;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RabbitMqProducer {

    private final RabbitTemplate rabbitTemplate;

    @CircuitBreaker(label = "rabbitmq")
    public void produceMessage(String message) {
        rabbitTemplate.convertAndSend("rabbitmq", message);
    }
}