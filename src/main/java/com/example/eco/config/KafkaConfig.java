package com.example.eco.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {

    public static final String TOPIC_INVENTORY = "inventory_updates";
    public static final String TOPIC_LOG = "inventory_logs";

    @Bean
    public NewTopic inventoryTopic() {
        return TopicBuilder.name(TOPIC_INVENTORY).partitions(1).replicas(1).build();
    }

    @Bean
    public NewTopic logTopic() {
        return TopicBuilder.name(TOPIC_LOG).partitions(1).replicas(1).build();
    }
}