package com.example.eco.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RabbitMQConfig {

    // Queue 1: Untuk update inventory
    public static final String QUEUE_INVENTORY = "inventory_updates";
    public static final String ROUTING_KEY_INVENTORY = "routing_inventory";

    // Queue 2: Untuk log/arsip pesan
    public static final String QUEUE_LOG = "inventory_logs";
    public static final String ROUTING_KEY_LOG = "routing_log";

    public static final String EXCHANGE = "exchange_utama";

    // Bean untuk Queue 1 (Durable diatur ke true untuk CloudAMQP)
    @Bean
    public Queue queueInventory() {
        return new Queue(QUEUE_INVENTORY, true);
    }

    // Bean untuk Queue 2 (Durable diatur ke true)
    @Bean
    public Queue queueLog() {
        return new Queue(QUEUE_LOG, true);
    }

    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(EXCHANGE);
    }

    @Bean
    public Binding bindingInventory(Queue queueInventory, TopicExchange exchange) {
        return BindingBuilder.bind(queueInventory).to(exchange).with(ROUTING_KEY_INVENTORY);
    }

    @Bean
    public Binding bindingLog(Queue queueLog, TopicExchange exchange) {
        return BindingBuilder.bind(queueLog).to(exchange).with(ROUTING_KEY_LOG);
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}