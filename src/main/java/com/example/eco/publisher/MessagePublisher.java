package com.example.eco.publisher;

import com.example.eco.config.KafkaConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class MessagePublisher {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    public void sendMessage(String message) {
        System.out.println("Mengirim pesan ke dua topik Kafka: " + message);

        // Kirim ke Topik Inventory
        kafkaTemplate.send(KafkaConfig.TOPIC_INVENTORY, message);

        // Kirim ke Topik Log
        kafkaTemplate.send(KafkaConfig.TOPIC_LOG, message);
    }
}