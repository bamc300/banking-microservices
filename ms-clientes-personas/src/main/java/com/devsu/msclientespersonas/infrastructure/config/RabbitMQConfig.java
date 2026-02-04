package com.devsu.msclientespersonas.infrastructure.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

  @Value("${rabbitmq.exchange.clients:clients.exchange}")
  private String exchange;

  @Value("${rabbitmq.queue.clients:clients.queue}")
  private String queue;

  @Value("${rabbitmq.routing.clients.created:clients.created}")
  private String routingKey;

  @Bean
  public TopicExchange clientsExchange() {
    return ExchangeBuilder.topicExchange(exchange).durable(true).build();
  }

  @Bean
  public Queue clientsQueue() {
    return QueueBuilder.durable(queue).build();
  }

  @Bean
  public Binding clientsBinding() {
    return BindingBuilder.bind(clientsQueue()).to(clientsExchange()).with(routingKey);
  }
}
