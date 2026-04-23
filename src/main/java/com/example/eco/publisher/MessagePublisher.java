package com.example.eco.publisher;

import com.example.eco.config.RabbitMQConfig;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MessagePublisher {

    @Autowired
    private RabbitTemplate template;

    public void sendMessage(String message) {
        System.out.println("Mengirim pesan ke dua channel: " + message);

        // Kirim ke Channel Inventory
        template.convertAndSend(RabbitMQConfig.EXCHANGE, RabbitMQConfig.ROUTING_KEY_INVENTORY, message);

        // Kirim ke Channel Log
        template.convertAndSend(RabbitMQConfig.EXCHANGE, RabbitMQConfig.ROUTING_KEY_LOG, message);
    }
}