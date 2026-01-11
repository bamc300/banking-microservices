package com.devsu.msclientespersonas.infrastructure.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

  @Value("${rabbitmq.exchange.clientes:clientes.exchange}")
  private String exchange;

  @Value("${rabbitmq.queue.clientes:clientes.queue}")
  private String queue;

  @Value("${rabbitmq.routing.clientes.creado:clientes.creado}")
  private String routingKey;

  @Bean
  public TopicExchange clientesExchange() {
    return ExchangeBuilder.topicExchange(exchange).durable(true).build();
  }

  @Bean
  public Queue clientesQueue() {
    return QueueBuilder.durable(queue).build();
  }

  @Bean
  public Binding clientesBinding() {
    return BindingBuilder.bind(clientesQueue()).to(clientesExchange()).with(routingKey);
  }
}

